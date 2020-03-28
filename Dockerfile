FROM ubuntu

RUN apt update
RUN apt install -y openjdk-8-jdk

RUN apt install -y software-properties-common
RUN add-apt-repository ppa:savoury1/ffmpeg4
RUN add-apt-repository ppa:savoury1/graphics
RUN add-apt-repository ppa:savoury1/multimedia
RUN apt update
RUN apt install -y ffmpeg

ARG JAR_FILE=build
COPY ${JAR_FILE} build
ENTRYPOINT ["java","-jar","build/libs/music-streaming-service-0.0.1-SNAPSHOT.jar"]