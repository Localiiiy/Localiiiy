import urllib.request
import json
import mimetypes
import uuid

def upload_gofile(file_path):
    # 1. Get server
    req = urllib.request.Request("https://api.gofile.io/servers", headers={"User-Agent": "Mozilla/5.0"})
    with urllib.request.urlopen(req) as resp:
        data = json.loads(resp.read().decode())
        server = data["data"]["servers"][0]["name"]
    print(f"Uploading to server: {server}")

    # 2. Prepare multipart body
    boundary = uuid.uuid4().hex
    filename = "Localiiiy.apk"
    with open(file_path, "rb") as f:
        file_bytes = f.read()

    body = (
        f"--{boundary}\r\n"
        f'Content-Disposition: form-data; name="file"; filename="{filename}"\r\n'
        f"Content-Type: application/vnd.android.package-archive\r\n\r\n"
    ).encode("utf-8") + file_bytes + f"\r\n--{boundary}--\r\n".encode("utf-8")

    upload_url = f"https://{server}.gofile.io/contents/uploadfile"
    req = urllib.request.Request(
        upload_url,
        data=body,
        headers={
            "Content-Type": f"multipart/form-data; boundary={boundary}",
            "User-Agent": "Mozilla/5.0"
        },
        method="POST"
    )

    with urllib.request.urlopen(req) as resp:
        res_json = json.loads(resp.read().decode())
        print("Upload Result:", json.dumps(res_json, indent=2))

if __name__ == "__main__":
    upload_gofile("/tmp/Localiiiy.apk")
