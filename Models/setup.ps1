param([string]$Python = "$PSScriptRoot/.runtime/python/python.exe")
$ErrorActionPreference = 'Stop'
if (-not (Test-Path -LiteralPath $Python)) {
    $Python = "$env:LOCALAPPDATA/Programs/Python/Python312/python.exe"
}
if (-not (Test-Path -LiteralPath $Python)) {
    throw 'Provide -Python with a standard Windows Python 3.12 executable.'
}
& $Python -m venv "$PSScriptRoot/.venv"
if ($LASTEXITCODE -ne 0) { throw 'Virtual environment creation failed' }
$venvPython = "$PSScriptRoot/.venv/Scripts/python.exe"
& $venvPython -m pip install --no-compile --upgrade pip
if ($LASTEXITCODE -ne 0) { throw 'pip upgrade failed' }
# CPU works without an NVIDIA driver. Install a matching CUDA torch/torchvision
# pair separately if GPU acceleration is needed.
& $venvPython -m pip install --no-compile torch==2.14.1 torchvision==0.29.1 --index-url https://download.pytorch.org/whl/cpu
if ($LASTEXITCODE -ne 0) { throw 'PyTorch installation failed' }
& $venvPython -m pip install --no-compile -r "$PSScriptRoot/requirements.txt"
if ($LASTEXITCODE -ne 0) { throw 'DeepLabCut installation failed' }
& $venvPython -m pip check
if ($LASTEXITCODE -ne 0) { throw 'Dependency compatibility check failed' }
