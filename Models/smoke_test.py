"""Run the real imported model on 12 frames, keeping artifacts in generated-videos."""
import json
from pathlib import Path
import subprocess
import sys


def main():
    import cv2
    import pandas as pd

    root = Path(__file__).resolve().parent
    output = root / "generated-videos" / "smoke-test"
    output.mkdir(parents=True, exist_ok=True)
    source = root / "AIMD Mcyntyre DLC Model" / "videos" / "FIRE37 EPM 02-27-26.mp4"
    clip = output / "sample.mp4"
    capture = cv2.VideoCapture(str(source))
    writer = None
    try:
        for index in range(12):
            ok, frame = capture.read()
            if not ok:
                raise RuntimeError(f"Cannot read frame {index} from {source}")
            if writer is None:
                writer = cv2.VideoWriter(str(clip), cv2.VideoWriter_fourcc(*"mp4v"),
                    30, (frame.shape[1], frame.shape[0]))
                if not writer.isOpened():
                    raise RuntimeError("Cannot create smoke-test video")
            writer.write(frame)
    finally:
        capture.release()
        if writer is not None:
            writer.release()
    command = [sys.executable, str(root / "deeplabcut_model.py"), "--video", str(clip),
               "--output-dir", str(output), "--device", "cpu"]
    with (output / "worker.log").open("w", encoding="utf-8") as log:
        completed = subprocess.run(command, stdout=subprocess.PIPE, stderr=log,
                                   text=True, timeout=600)
    result = json.loads(completed.stdout)
    if completed.returncode != 0 or result["status"] != "completed":
        raise RuntimeError(f"Worker failed: {result}; see {output / 'worker.log'}")
    predictions = pd.read_hdf(result["h5"])
    assert len(predictions) == 12, f"Expected 12 predictions, got {len(predictions)}"
    assert len(predictions.columns) == 24, "Expected 8 bodyparts with x, y, and likelihood"
    assert predictions.notna().all().all(), "Model returned missing predictions"
    likelihoods = predictions.xs("likelihood", axis=1, level="coords")
    assert ((likelihoods >= 0) & (likelihoods <= 1)).all().all()
    capture = cv2.VideoCapture(result["labeled_video"])
    try:
        frames = 0
        while capture.read()[0]:
            frames += 1
        assert frames == 12, f"Expected 12 labeled frames, got {frames}"
    finally:
        capture.release()
    missing = subprocess.run([sys.executable, str(root / "deeplabcut_model.py"),
        "--video", str(output / "missing.mp4")], capture_output=True, text=True)
    assert missing.returncode == 1
    assert json.loads(missing.stdout)["status"] == "failed"
    print(json.dumps({"smoke_test": "passed", "frames": frames, "result": result}, indent=2))


if __name__ == "__main__":
    main()
