#!/usr/bin/env python3
"""Print the KSU_EXPECTED_SIZE / KSU_EXPECTED_HASH values for kernel/Kbuild.

The kernel only treats an APK as the KernelSU manager when both its package name
(`KSU_PACKAGE_NAME`) and its signing certificate match. A fork is signed with its
own keystore, so the expected signature baked into the kernel must be regenerated.

Run this with the same keystore used to sign the forked manager APK, then paste the
output into kernel/Kbuild (or pass it as KSU_EXPECTED_SIZE / KSU_EXPECTED_HASH when
building the kernel module).
"""

import argparse
import hashlib
import os
import subprocess
import tempfile
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parent.parent
KBUILD = REPO_ROOT / "kernel" / "Kbuild"


def export_certificate(keystore: Path, alias: str, storepass: str, keypass: str) -> bytes:
    with tempfile.TemporaryDirectory() as tmp:
        der = os.path.join(tmp, "cert.der")
        subprocess.run(
            [
                "keytool",
                "-exportcert",
                "-alias", alias,
                "-keystore", str(keystore),
                "-storepass", storepass,
                "-keypass", keypass,
                "-file", der,
            ],
            check=True,
        )
        return Path(der).read_bytes()


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("keystore", type=Path, help="path to the keystore (.jks) used to sign the manager APK")
    parser.add_argument("alias", help="key alias inside the keystore")
    parser.add_argument("--storepass", default=os.environ.get("KEYSTORE_PASSWORD", ""), help="keystore password")
    parser.add_argument("--keypass", default=None, help="key password (defaults to the keystore password)")
    args = parser.parse_args()

    storepass = args.storepass
    keypass = args.keypass if args.keypass is not None else storepass

    certificate = export_certificate(args.keystore, args.alias, storepass, keypass)
    size = len(certificate)
    digest = hashlib.sha256(certificate).hexdigest()

    print(f"-- certificate size: {size} bytes")
    print(f"KSU_EXPECTED_SIZE ?= 0x{size:04x}")
    print(f"KSU_EXPECTED_HASH ?= {digest}")
    print(f"-- merged into: {KBUILD}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
