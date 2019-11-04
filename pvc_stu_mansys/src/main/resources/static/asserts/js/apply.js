/**
 * 申请详情/申请处理
 * 作者：辰湖飞彦
 */
function applyDesc(
    user_source,
    token,
    id
) {
    // 设置消息参数
    var message_type = 3;
    var data = id + "";
    var has_view = yes_view;
    var toService = applyService;
    // 发送消息
    send(null, user_source, message_type, data, has_view, toService, token);

    return false;
}

/**
 * 同意一个申请
 * 作者：辰湖飞彦
 */
function accessApply(
    user_source,
    token,
    id
) {
    var b = confirm("确定同意吗？你还可以点击详情按钮查看这个申请的详情斟酌斟酌哦~");
    if(b){
        // 设置消息参数
        var message_type = 4;
        var data = id + "";
        var has_view = yes_view;
        var toService = applyService;
        // 发送消息
        send(null, user_source, message_type, data, has_view, toService, token);
    }

    return false;
}

/**
 * 拒绝一个申请
 * 作者：辰湖飞彦
 */
function rejectApply(
    user_source,
    token,
    id
) {
    var b = confirm("确定拒绝吗？你还可以点击详情按钮查看这个申请的详情斟酌斟酌哦~");
    if(b){
        // 设置消息参数
        var message_type = 5;
        var data = id + "";
        var has_view = yes_view;
        var toService = applyService;
        // 发送消息
        send(null, user_source, message_type, data, has_view, toService, token);
    }

    return false;
}

/**
 * 删除一个申请
 * 作者：辰湖飞彦
 */
function applyRm(
    user_source,
    token,
    id
) {
    var b = confirm("数据无价，你想好了？");
    if(b){
        // 设置消息参数
        var message_type = 2;
        var data = id + "";
        var has_view = yes_view;
        var toService = applyService;
        // 发送消息
        send(null, user_source, message_type, data, has_view, toService, token);
    }

    return false;
}