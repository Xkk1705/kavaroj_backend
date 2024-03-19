package com.xukang.codesandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.LogContainerResultCallback;

import java.util.List;

/**
 * 演示java客户端操作docker
 */
public class DockerDemo {
    public static void main(String[] args) throws InterruptedException {
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        // 测试连接
//        PingCmd pingCmd = dockerClient.pingCmd();
//        pingCmd.exec();

        // 拉取镜像
        String image = "nginx:latest";
//        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
//        //拉取镜像回调函数
//        PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
//            @Override
//            public void onNext(PullResponseItem item) {
//                // 打印完成拉取后的状态
//                System.out.println(item.getStatus());
//            }
//        };
//        pullImageCmd
//                .exec(pullImageResultCallback)
//                .awaitCompletion();// 等待完成
//        System.out.println("拉取完成");

        // 创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        CreateContainerResponse createContainerResponse = containerCmd
                .withCmd("echo", "hello docker")
                .exec();
        System.out.println(createContainerResponse);

        // 查看容器
        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
        List<Container> containerList = listContainersCmd.withShowAll(true).exec();
        for (Container container : containerList) {
            System.out.println(container.getImage());
        }

        // 启动容器
        String containerId = createContainerResponse.getId();
        dockerClient.startContainerCmd(containerId).exec();

//        // 查看日志
//        // 输出日志回调函数
//        LogContainerResultCallback logContainerResultCallback = new LogContainerResultCallback() {
//            @Override
//            public void onNext(Frame item) {
//                String log = new String(item.getPayload());
//                System.out.println("日志：" + log);
//            }
//        };
//        dockerClient
//                .logContainerCmd(containerId)
//                .withStdOut(true)
//                .withStdErr(true)
//                .exec(logContainerResultCallback)
//                .awaitCompletion();

//        // 删除容器
//        dockerClient.removeContainerCmd(containerId).withForce(true).exec();
//        // 删除镜像
//        dockerClient.removeImageCmd(image).withForce(true).exec();
    }
}
