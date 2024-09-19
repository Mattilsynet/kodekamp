# Go webserver

Basic http server with a muxer and static fileserver.


## Usage

```bash
go run main.go
```

Get static index content:
```bash
curl http://localhost:8080/
```

Post a json payload:
```bash
curl -X POST http://localhost:8080/action \
  -H "Content-Type: application/json" \
  -d '{"action": "hit me!"}'
```
