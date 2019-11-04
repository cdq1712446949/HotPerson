package com.chenu.pvcstumansys.controller;

import com.chenu.pvcstumansys.db.bean.Message;
import com.chenu.pvcstumansys.db.bean.noindb.MessageData;
import com.chenu.pvcstumansys.service.MessageService;
import com.chenu.pvcstumansys.utils.GoUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * 基本作用：消息处理者/消息生产者
 * 详细解释：完成消息通信的处理，同时包含消息数据库的相关操作，同时，其也是消息的生产者
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
@Controller
public class MessageController {

    /**
     * 文件上传相关
     */
    @Value("${upload.picture.path}")
    public static String upload_Picture_Path;
    @javax.annotation.Resource
    private ResourceLoader resourceLoader;
    /**
     * 上传的文件
     */
    public static MultipartFile UPLOAD_FILE;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MessageService messageService;

    @Autowired
    private RedisTemplate redisTemplate;

    public MessageController() {
    }

    /**
     * 发送消息一条消息，不携带数据，用作测试
     */
    @GetMapping("/send/{user_source}/{message_type}/{has_view}/{toService}")
    @ResponseBody
    public Message sendMessageNoData(
            @PathVariable("user_source") int user_source,
            @PathVariable("message_type") int message_type,
            @PathVariable("has_view") int has_view,
            @PathVariable("toService") int toService
    ){
        logger.info("sendMessageNoData");
        // 第一步，先封装消息
        Message message = new Message();
        message.setfUserSource(user_source);
        message.setMesstype(message_type);
        message.setHasView((byte)has_view);
        // 好的，现在可以发送消息啦！ ^ - ^
        Message result = messageService.sendAndReceive(message, toService);
        return result;
    }

    /**
     * 发送一条携带数据的消息，当然，不给也可以不带
     * 返回一条处理结果消息，以json的格式
     */
    @PostMapping("/send/nv")
    @ResponseBody
    public Message sendNoView(
            Message message,
            MultipartFile file       /* 上传的文件 */
    ){
        logger.info("请求不需要视图的消息服务... ");
        logger.info("请求消息 --> " + message.toString());
        logger.info("请求的服务号 --> " + message.getToService());
        Message result = new Message();
        MessageData messageData = new MessageData();
        Gson gson = new Gson();
        // 设置上传的文件
        MessageController.UPLOAD_FILE = file;
        if(file != null)
            logger.info("上传的文件名称 --> " + file.getOriginalFilename());

        if(message.getHasView().equals(Message.NO_VIEW)){
            result = messageService.sendAndReceive(message, message.getToService());
            // 判断是否服务成功
            if(result != null){

                if(result.getMesstype() == Message.HANDLER_SUCCESS){
                    messageData.setUrl(result.getData());
                } else {
                    // 失败了
                    messageData.setNote("error" + Message.ERROR_NORMAL +
                            ": 一般性错误，" +
                            result.getData());
                }

            } else {
                // 收不到回发处理消息
                result = new Message();
                messageData.setNote("error" + Message.ERROR_NO_RESPOND +
                        ": 服务未响应，服务失败...");
            }

        } else {
            // 说明需要视图，请求错了方式
            messageData.setNote("error" + Message.ERROR_REQUEST_TYPE +
                    ": 错误的请求方式，需要视图请通过'/send/wv'的方式请求...");
        }
        // 放入消息数据
        result.setData(gson.toJson(messageData));
        return result;
    }

    /**
     * 发送一条携带数据的消息，当然，不给也可以不带
     * 返回一个网页视图
     */
    @PostMapping("/send/wv")
    public String sendWithView(
            Message message,
            Model model,
            MultipartFile file       /* 上传的文件 */
    ){
        logger.info("请求需要视图的消息服务... ");
        logger.info("请求消息 --> " + message.toString());
        logger.info("请求的服务号 --> " + message.getToService());
        Message result;
        MessageData messageData = new MessageData();
        Gson gson = new Gson();
        // 设置上传的文件
        MessageController.UPLOAD_FILE = file;
        if(file != null)
            logger.info("上传的文件名称 --> " + file.getOriginalFilename());

        if(message.getHasView().equals(Message.YES_VIEW)){
            result = messageService.sendAndReceive(message, message.getToService());
            // 判断是否服务成功
            if(result != null){

                if(result.getMesstype() == Message.HANDLER_SUCCESS){
                    // 成功直接返回视图
                    logger.info("正在返回视图 --> " + result.getData());
                    return GoUtils.RedirectUrl(result.getData());
                } else {
                    // 失败了
                    messageData.setNote("error" + Message.ERROR_NORMAL +
                            ": 一般性错误，" +
                            result.getData());
                }

            } else {
                // 收不到回发处理消息
                messageData.setNote("error" + Message.ERROR_NO_RESPOND +
                        ": 服务未响应，服务失败...");
            }

        } else {
            // 说明不需要视图，请求错了方式
            messageData.setNote("error" + Message.ERROR_REQUEST_TYPE +
                    ": 错误的请求方式，不需要视图请通过'/send/nv'的方式请求...");
        }
        // 失败返回error页面
        model.addAttribute("err_msg", messageData);
        return "myerror";
    }

}
