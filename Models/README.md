# DeepLabCut video worker

Git includes the inference configs and `snapshot-best-020.pt` checkpoint. Training
images, datasets, original videos, evaluation outputs, duplicate exports, older
checkpoints, and training logs are excluded by `Models/.gitignore` and remain
local. This avoids the long evaluation-image paths that prevented committing
on Windows. The inference worker does not need those excluded files. The smoke
test uses a local original video and therefore requires that video to be supplied
on a fresh checkout.

`deeplabcut_model.py` runs the imported single-animal PyTorch project using its
latest trained checkpoint. It writes a config per job and supplies the imported
project as an absolute `modelprefix`, because DLC resolves the config's project
root from its directory. The imported model is preserved. The frontend and
backend sources are unchanged.

## Setup (Windows PowerShell)

Use standard Windows CPython 3.12, not MSYS Python. A local installation can live
in `.runtime/python`; setup also detects a per-user `Python312` installation.
Install dependencies in an isolated environment:

```powershell
.\Models\setup.ps1
# Or: .\Models\setup.ps1 -Python 'C:\path\to\python.exe'
```

The default installation uses CPU PyTorch. GPU use requires a compatible NVIDIA
driver and CUDA-enabled torch/torchvision wheels. TensorFlow and the DLC GUI are
unnecessary for this project.

`requirements.lock.txt` records the tested Windows CPU environment. To reproduce
all of its versions after creating the virtual environment:

```powershell
& .\Models\.venv\Scripts\python.exe -m pip install --no-compile -r .\Models\requirements.lock.txt
```

## Process an uploaded video

From the repository root:

```powershell
& .\Models\.venv\Scripts\python.exe .\Models\deeplabcut_model.py `
  --video 'C:\uploads\rat.mp4' --device cpu
```

Optional arguments: `--output-dir`, `--config`, `--shuffle` (default 1),
`--trainingsetindex` (default 0), and `--batch-size` (default 1).
`--device auto` uses CUDA when available, otherwise CPU.

Every invocation creates `Models/generated-videos/<unique-job-id>/` containing:

- `labeled.mp4`: H.264 video with predicted bodypart positions drawn on each frame,
  encoded for browser playback using the bundled FFmpeg executable
- `*.csv` and `*.h5`: coordinates and confidence values
- `config.yaml`: relocated project config
- `result.json`: absolute artifact paths and job identifier

stdout contains exactly one JSON object on success or processing failure.
DeepLabCut logs and tracebacks go to stderr. Exit code 0 means completed, 1 means
processing failed, and argparse uses 2 for invalid command arguments. A failed
job can leave partial files; only publish outputs after exit code 0. Generated
files, environments, and downloaded runtimes are ignored by Git.

## Calling from Spring Boot

Save a multipart upload on the server first, then pass its absolute local path.
Use `ProcessBuilder` arguments individually to handle spaces safely. Configure
absolute Python, script, upload, and output paths; working directories differ
between IDE launches and packaged deployments.

```java
ProcessBuilder builder = new ProcessBuilder(
    pythonPath, // absolute Models/.venv/Scripts/python.exe
    scriptPath, // absolute Models/deeplabcut_model.py
    "--video", uploadedVideo.toAbsolutePath().toString(),
    "--output-dir", generatedVideos.toAbsolutePath().toString(),
    "--device", "cpu"
);
// Do not merge stderr with stdout: stdout is the JSON response.
builder.redirectError(logFile.toFile());
Process process = builder.start();
boolean completed;
try {
    completed = process.waitFor(60, java.util.concurrent.TimeUnit.MINUTES);
} catch (InterruptedException ex) {
    process.destroyForcibly();
    Thread.currentThread().interrupt();
    throw ex;
}
if (!completed) {
    process.destroyForcibly();
    throw new IllegalStateException("DeepLabCut processing timed out");
}
String json = new String(process.getInputStream().readAllBytes(),
    java.nio.charset.StandardCharsets.UTF_8);
if (process.exitValue() != 0) {
    throw new IllegalStateException("DeepLabCut failed: " + json);
}
// Parse json with your configured JSON mapper; store labeled_video and csv.
```

Create the log directory before starting. The result is small enough to fit in
the process pipe; all verbose logs are redirected to disk. Execute long jobs
through a bounded background queue, return a job ID from the upload endpoint,
and expose job status and authorized downloads. Avoid running many model
processes concurrently on one GPU. The script processes files; HTTP upload
routes and database `Video`/`Csv` persistence still belong in the backend.

To store output somewhere else, provide `--output-dir` with the backend's
configured generated-video directory. Returned file paths are server paths,
not browser URLs: serve them through an authenticated download endpoint.

## Backend dependencies

The existing POM specifies Java 24 and Spring Boot 4.0.5. Maven downloads the
Java libraries already declared there; no Python library goes in the POM.
The installed JDK 26 successfully compiled this project's Java 24 target.
A locally downloaded Maven can run with:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-26.0.2.1'
& .\Models\.runtime\apache-maven-3.9.14\bin\mvn.cmd `
  -f .\mcintyre-lab-backend\pom.xml `
  '-Dmaven.repo.local=Models/.runtime/maven-repository' -DskipTests package
```

The backend also needs a configured PostgreSQL database, JWT secret, and SMTP
settings from `application.properties.example` before the full application can
start. Those service credentials are separate from dependency installation.

## Verification

The backend packaged successfully with the installed JDK 26 and Maven 3.9.14
(`-DskipTests package`; application startup requires the service settings above).
The real model `snapshot-best-020.pt` passed a CPU smoke test on 12 frames from
an imported rat video. All eight bodyparts produced nonmissing coordinates and
confidence values in [0, 1], and the H.264 output decoded to 12 frames. The
worker also returned exit code 1 and valid JSON for a missing input file.
This checks execution and file generation; it does not measure model accuracy
on new videos or validate a full-length video job.

Run the same test again with:

```powershell
& .\Models\.venv\Scripts\python.exe .\Models\smoke_test.py
```

Official installation and API documentation:
https://deeplabcut.github.io/DeepLabCut/docs/installation.html
https://deeplabcut.github.io/DeepLabCut/docs/standardDeepLabCut_UserGuide
