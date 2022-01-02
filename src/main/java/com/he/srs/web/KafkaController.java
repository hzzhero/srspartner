package com.he.srs.web;

import com.he.srs.bean.vo.BytePicMsg;
import com.he.srs.bean.vo.ResponseVo;
import com.he.srs.util.BuildResponseUtils;
import com.he.srs.util.KafkaUtils;
import com.he.srs.util.ProtoStaffUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * kafka控制器
 *
 * @author 154594742@qq.com
 * @date 2021/3/2 15:01
 */

@RestController
@Api(tags = "Kafka控制器")
@Slf4j
public class KafkaController {

    @Autowired
    private KafkaUtils kafkaUtils;

    /**
     * 新增topic (支持批量，这里就单个作为演示)
     *
     * @param topic topic
     * @return ResponseVo
     */
    @ApiOperation("新增topic")
    @PostMapping("kafka")
    public ResponseVo<?> add(String topic) {
        NewTopic newTopic = new NewTopic(topic, 3, (short) 1);
        kafkaUtils.createTopic(Arrays.asList(newTopic));
        return BuildResponseUtils.success();
    }

    /**
     * 查询topic信息 (支持批量，这里就单个作为演示)
     *
     * @param topic 自增主键
     * @return ResponseVo
     */
    @ApiOperation("查询topic信息")
    @GetMapping("kafka/{topic}")
    public ResponseVo<String> getBytTopic(@PathVariable String topic) {
        return BuildResponseUtils.buildResponse(kafkaUtils.getTopicInfo(Arrays.asList(topic)));
    }

    /**
     * 删除topic (支持批量，这里就单个作为演示)
     * (注意：如果topic正在被监听会给人感觉删除不掉（但其实是删除掉后又会被创建）)
     *
     * @param topic topic
     * @return ResponseVo
     */
    @ApiOperation("删除topic")
    @DeleteMapping("kafka/{topic}")
    public ResponseVo<?> delete(@PathVariable String topic) {
        kafkaUtils.deleteTopic(Arrays.asList(topic));
        return BuildResponseUtils.success();
    }

    /**
     * 查询所有topic
     *
     * @return ResponseVo
     */
    @ApiOperation("查询所有topic")
    @GetMapping("kafka/allTopic")
    public ResponseVo<List<String>> getAllTopic() {
        return BuildResponseUtils.buildResponse(kafkaUtils.getAllTopic());
    }

    /**
     * 生产者往topic中发送消息demo
     *
     * @param topic
     * @param message
     * @return
     */
    @ApiOperation("往topic发送消息")
    @PostMapping("kafka/message")
    public ResponseVo<?> sendMessage(String topic, String message) {
        kafkaUtils.sendMessage(topic, message);
        return BuildResponseUtils.success();
    }

    /**
     * 消费者示例demo
     * <p>
     * 基于注解监听多个topic，消费topic中消息
     * （注意：如果监听的topic不存在则会自动创建）
     */
    @KafkaListener(topics = {"kafka-test-2"})
    public void consume(byte[] message) {

        BytePicMsg bytePicMsg = ProtoStaffUtil.deserializeMsg(message, BytePicMsg.class);
        long timestamp = bytePicMsg.getTimestamp();
        long timeMillis = System.currentTimeMillis();
        log.info("时间：{}----{}----{}",timestamp,timeMillis,timeMillis-timestamp);
//        FileUtil.writeBytes(bytePicMsg.getImageData().toByteArray(), "D:/pic/"+timestamp+".png");

    }
}
