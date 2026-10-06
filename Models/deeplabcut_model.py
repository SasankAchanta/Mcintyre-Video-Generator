"""Headless DeepLabCut worker. stdout is one JSON result; logs go to stderr."""
from __future__ import annotations

import argparse
import contextlib
import json
import os
from pathlib import Path
import sys
import subprocess
import traceback
import uuid

MODELS = Path(__file__).resolve().parent
DEFAULT_CONFIG = MODELS / "AIMD Mcyntyre DLC Model" / "config.yaml"


def run(args):
    config_path = args.config.resolve(strict=True)
    video = args.video.resolve(strict=True)
    if not video.is_file() or video.suffix.lower() not in {".mp4", ".avi", ".mov", ".mkv", ".m4v"}:
        raise ValueError("Input must be an existing MP4, AVI, MOV, MKV, or M4V file")
    import yaml
    import cv2
    import torch
    with config_path.open(encoding="utf-8") as stream:
        config = yaml.safe_load(stream)
    if config.get("engine") != "pytorch" or config.get("multianimalproject"):
        raise ValueError("This worker requires a single-animal PyTorch DeepLabCut project")
    if args.device == "cuda" and not torch.cuda.is_available():
        raise ValueError("CUDA was requested but is unavailable; use --device cpu or auto")
    device = "cuda" if args.device == "auto" and torch.cuda.is_available() else args.device
    if device == "auto":
        device = "cpu"
    project = config_path.parent
    fraction = config["TrainingFraction"][args.trainingsetindex]
    model_folder = project / "dlc-models-pytorch" / f"iteration-{config['iteration']}" / (
        f"{config['Task']}{config['date']}-trainset{int(fraction * 100)}shuffle{args.shuffle}"
    )
    train = model_folder / "train"
    if not (train / "pytorch_config.yaml").is_file() or not list(train.glob("snapshot*.pt")):
        raise FileNotFoundError(f"Missing model configuration or trained checkpoint in {train}")
    capture = cv2.VideoCapture(str(video))
    try:
        readable, frame = capture.read()
        if not readable or frame is None:
            raise ValueError("OpenCV cannot decode the input video")
    finally:
        capture.release()

    # Each invocation gets its own directory, avoiding cached analysis and name collisions.
    job_id = uuid.uuid4().hex
    output = args.output_dir.resolve() / job_id
    output.mkdir(parents=True, exist_ok=False)
    # DLC 3.0.2 always resolves project_path from the config's parent. An absolute
    # modelprefix points both inference and labeling at the original checkpoints.
    config["project_path"] = str(output)
    config["snapshotindex"] = -1
    runtime_config = output / "config.yaml"
    with runtime_config.open("w", encoding="utf-8") as stream:
        yaml.safe_dump(config, stream, sort_keys=False)

    os.environ["DLClight"] = "True"
    os.environ.setdefault("MPLBACKEND", "Agg")
    import deeplabcut
    print(f"Processing {video.name} on {device}; job {job_id}", file=sys.stderr)
    deeplabcut.analyze_videos(
        str(runtime_config), [str(video)], shuffle=args.shuffle,
        trainingsetindex=args.trainingsetindex, save_as_csv=True,
        destfolder=str(output), device=device, batch_size=args.batch_size,
        modelprefix=str(project),
    )
    deeplabcut.create_labeled_video(
        str(runtime_config), [str(video)], shuffle=args.shuffle,
        trainingsetindex=args.trainingsetindex, destfolder=str(output),
        max_workers=1,
        modelprefix=str(project),
    )
    labeled = sorted(output.glob("*labeled.mp4"))
    csv = sorted(output.glob("*.csv"))
    h5 = sorted(output.glob("*.h5"))
    if len(labeled) != 1 or len(csv) != 1 or len(h5) != 1:
        raise RuntimeError(f"Expected one labeled MP4, CSV, and H5 in {output}")
    if any(path.stat().st_size == 0 for path in labeled + csv + h5):
        raise RuntimeError("DeepLabCut produced an empty output file")
    # DeepLabCut's mp4v codec is not widely playable in browsers. Bundle FFmpeg
    # through imageio-ffmpeg and publish H.264/YUV420p with streaming metadata.
    import imageio_ffmpeg
    browser_video = output / "labeled.mp4"
    subprocess.run([
        imageio_ffmpeg.get_ffmpeg_exe(), "-nostdin", "-y", "-loglevel", "error",
        "-i", str(labeled[0]), "-vf", "pad=ceil(iw/2)*2:ceil(ih/2)*2",
        "-c:v", "libx264", "-preset", "fast", "-crf", "18",
        "-pix_fmt", "yuv420p", "-movflags", "+faststart", str(browser_video),
    ], check=True, stdout=sys.stderr, stderr=sys.stderr)
    if not browser_video.is_file() or browser_video.stat().st_size == 0:
        raise RuntimeError("FFmpeg did not produce a browser-compatible video")
    labeled[0].unlink()
    result = {
        "status": "completed", "job_id": job_id, "input_video": str(video),
        "output_dir": str(output), "labeled_video": str(browser_video),
        "csv": str(csv[0]), "h5": str(h5[0]), "device": device,
    }
    (output / "result.json").write_text(json.dumps(result, indent=2), encoding="utf-8")
    return result


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--video", type=Path, required=True, help="Uploaded video's absolute local path")
    parser.add_argument("--config", type=Path, default=DEFAULT_CONFIG)
    parser.add_argument("--output-dir", type=Path, default=MODELS / "generated-videos")
    parser.add_argument("--device", choices=("auto", "cpu", "cuda"), default="auto")
    parser.add_argument("--shuffle", type=int, default=1)
    parser.add_argument("--trainingsetindex", type=int, default=0)
    parser.add_argument("--batch-size", type=int, default=1)
    args = parser.parse_args()
    try:
        if args.batch_size < 1 or args.shuffle < 1 or args.trainingsetindex < 0:
            raise ValueError("Batch size and shuffle must be positive; training set index must be nonnegative")
        with contextlib.redirect_stdout(sys.stderr):
            result = run(args)
    except Exception as exc:
        traceback.print_exc(file=sys.stderr)
        print(json.dumps({"status": "failed", "error": str(exc)}), flush=True)
        return 1
    print(json.dumps(result), flush=True)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
