import posixpath
import socket
import sys
import tarfile
import tempfile
from pathlib import Path

import paramiko


EXCLUDE_PARTS = {
    ".git",
    ".github",
    ".nuxt",
    ".output",
    "node_modules",
    "coverage",
    "target",
}

EXCLUDE_SUFFIXES = {".log"}


def should_skip(path: Path, root: Path) -> bool:
    rel = path.relative_to(root)
    if any(part in EXCLUDE_PARTS for part in rel.parts):
        return True
    if path.name == ".env" or path.name.startswith(".env."):
        return True
    if path.name.startswith("_isinwheel_") and path.suffix == ".html":
        return True
    if path.suffix in EXCLUDE_SUFFIXES:
        return True
    return False


def build_archive(root: Path) -> Path:
    temp = tempfile.NamedTemporaryFile(delete=False, suffix=".tar.gz")
    temp.close()
    archive_path = Path(temp.name)
    with tarfile.open(archive_path, "w:gz") as tar:
        for path in root.rglob("*"):
            if should_skip(path, root):
                continue
            tar.add(path, arcname=path.relative_to(root))
    return archive_path


def ensure_remote_dir(sftp: paramiko.SFTPClient, remote_path: str) -> None:
    parts = [part for part in remote_path.split("/") if part]
    current = "/"
    for part in parts:
        current = posixpath.join(current, part)
        try:
            sftp.stat(current)
        except IOError:
            sftp.mkdir(current)


def load_private_key(key_path: str):
    key_file = Path(key_path).expanduser()
    key_classes = [
        paramiko.Ed25519Key,
        paramiko.RSAKey,
        paramiko.ECDSAKey,
    ]
    dss_key = getattr(paramiko, "DSSKey", None)
    if dss_key is not None:
        key_classes.append(dss_key)
    for key_cls in key_classes:
        try:
            return key_cls.from_private_key_file(str(key_file))
        except paramiko.SSHException:
            continue
    raise ValueError(f"Unsupported private key format: {key_file}")


def safe_print(text: str, stream=None) -> None:
    stream = stream or sys.stdout
    encoding = getattr(stream, "encoding", None) or "utf-8"
    stream.write(text.encode(encoding, errors="replace").decode(encoding, errors="replace"))
    if not text.endswith("\n"):
        stream.write("\n")


def main() -> int:
    if len(sys.argv) < 4:
        print(
            "Usage: py deploy/remote/deploy_remote.py <host> <user> <password-or-dash> [remote_dir] [private_key_path]",
            file=sys.stderr,
        )
        return 1

    host, user, password = sys.argv[1], sys.argv[2], sys.argv[3]
    remote_dir = sys.argv[4] if len(sys.argv) > 4 else "/opt/shop"
    private_key_path = sys.argv[5] if len(sys.argv) > 5 else None
    root = Path(__file__).resolve().parents[2]

    try:
        with socket.create_connection((host, 22), timeout=10):
            pass
    except OSError as exc:
        print(f"SSH to {host}:22 is unreachable: {exc}", file=sys.stderr)
        return 2

    archive_path = build_archive(root)
    try:
        client = paramiko.SSHClient()
        client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        connect_kwargs = {
            "hostname": host,
            "username": user,
            "timeout": 20,
        }
        if private_key_path:
            connect_kwargs["pkey"] = load_private_key(private_key_path)
        else:
            connect_kwargs["password"] = password
        client.connect(**connect_kwargs)
        sftp = client.open_sftp()
        ensure_remote_dir(sftp, remote_dir)
        remote_archive = posixpath.join(remote_dir, "release.tar.gz")
        sftp.put(str(archive_path), remote_archive)
        sftp.close()

        commands = [
            f"mkdir -p {remote_dir}",
            f"tar -xzf {remote_archive} -C {remote_dir}",
            f"chmod +x {remote_dir}/deploy/remote/bootstrap-server.sh",
            f"bash {remote_dir}/deploy/remote/bootstrap-server.sh {remote_dir}",
        ]
        for cmd in commands:
            stdin, stdout, stderr = client.exec_command(cmd)
            exit_code = stdout.channel.recv_exit_status()
            out = stdout.read().decode("utf-8", errors="replace")
            err = stderr.read().decode("utf-8", errors="replace")
            if out:
                safe_print(out)
            if err:
                safe_print(err, stream=sys.stderr)
            if exit_code != 0:
                print(f"Command failed: {cmd}", file=sys.stderr)
                return exit_code
        client.close()
    finally:
        if archive_path.exists():
            archive_path.unlink()

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
