FROM ubuntu:latest
LABEL authors="r.williams"

ENTRYPOINT ["top", "-b"]