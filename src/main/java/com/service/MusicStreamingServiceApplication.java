package com.service;

import com.service.entity.FileModificationSpecification;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.service")
public class MusicStreamingServiceApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext run = SpringApplication.run(MusicStreamingServiceApplication.class, args);

/*		FileModificationCommandBuilder fileModificationCommandBuilder = run.getBean(FileModificationCommandBuilder.class);
		FileModificationCommandExecutor executor = run.getBean(FileModificationCommandExecutor.class);

		DefaultFileCommand silence = fileModificationCommandBuilder.buildModificationQuery(FileModificationSpecification.builder().startTime(0L)
						.firstFilePath("src/main/resources/ffmpeg-api/bin/asdasd.mp4").durationTime(120L).isRemoveAudio(true)
						.audioCodec("copy").videoCodec("copy").resultFilePath("src/main/resources/ffmpeg-api/bin/out-sil.mp4").build());

		DefaultFileCommand defaultFileCommand = fileModificationCommandBuilder.buildModificationQuery(FileModificationSpecification.builder().startTime(0L)
						.firstFilePath("src/main/resources/ffmpeg-api/bin/mus.mp3")
						.secondFilePath("src/main/resources/ffmpeg-api/bin/out-sil.mp4").durationTime(120L).audioCodec("copy").videoCodec("copy")
						.resultFilePath("src/main/resources/ffmpeg-api/bin/out-res.mp4").build());

		executor.executeCommand(silence);
		executor.executeCommand(defaultFileCommand);*/

    FileModificationCommandBuilder ю = run.getBean(FileModificationCommandBuilder.class);
    FileModificationCommandExecutor executor = run.getBean(FileModificationCommandExecutor.class);

    FileModificationSpecification silence = run.getBean("removeAudioFromFileSpecification", FileModificationSpecification.class);
    silence.setFirstFilePath("src/main/resources/ffmpeg-api/bin/mus.mp3");
    silence.setSecondFilePath("src/main/resources/ffmpeg-api/bin/asdasd.mp4");
    silence.setResultFilePath("src/main/resources/ffmpeg-api/bin/out-sil.mp4");

    FileModificationSpecification mergeInLoop = run.getBean("mergeLoopedVideoBeforeAudioFinishSpecification", FileModificationSpecification.class);
    mergeInLoop.setFirstFilePath("src/main/resources/ffmpeg-api/bin/out-sil.mp4");
    mergeInLoop.setSecondFilePath("src/main/resources/ffmpeg-api/bin/mus.mp3");
    mergeInLoop.setResultFilePath("src/main/resources/ffmpeg-api/bin/res.mp4");

    FileModificationSpecification videoToStream = run.getBean("videoToStream", FileModificationSpecification.class);
    videoToStream.setFirstFilePath("src/main/resources/ffmpeg-api/bin/res.mp4");
    videoToStream.setResultFilePath("src/main/resources/static/1/result-stream.m3u8");
/*
    executor.executeCommand(fileModificationCommandBuilder.buildModificationQuery(silence));
    executor.executeCommand(fileModificationCommandBuilder.buildModificationQuery(mergeInLoop));
    executor.executeCommand(fileModificationCommandBuilder.buildModificationQuery(videoToStream));

    videoToStream.setFirstFilePath("src/main/resources/ffmpeg-api/bin/media.mp4");
    videoToStream.setResultFilePath("src/main/resources/static/2/result-stream.m3u8");
    executor.executeCommand(fileModificationCommandBuilder.buildModificationQuery(videoToStream));
*/

  }

}
