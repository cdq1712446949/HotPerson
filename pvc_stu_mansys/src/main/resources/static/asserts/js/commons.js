/**
 * 公共参数
 */
var applyService = -1;
var userService = 0;
var professionalService = 1;
var sch2profService = 2;
var schoolService = 3;
var studentService = 4;
var replyService = 5;
var no_view = 0;
var yes_view = 1;

/**
 * 通知栏数据改变ajax后台处理函数支持
 */
function notificationChangeSuccessHandler(
    result
) {
    console.log("ajax结果--> ");
    console.log(result);
    // 解析回复数据
    var replyList = result.tag.rows;
    var replyTotal = result.tag.total;
    var notificationBar = $('#notificationBar');
    var context = notificationBar.find('ul');
    // 有通知才解析并展示
    if(replyTotal > 0){
        // 遍历数据
        context.empty();
        context.append('<li class="nav-header notification-header"><h5>通知栏</h5></li>\n');
        for(var i = 0; i < replyTotal; i++){
            var reply = replyList[i];
            // 将数据放到网页上
            var item = '<li class="nav-item notification-item"><a style="display : inline-block;" class="nav-link active" href="#">' + reply.note + '</a><a style="display : inline-block; float: right;" href="#" class="btn btn-sm btn-danger deleteBtn" onclick="return replyfRm(' + final_user_source + ',\'' + final_token + '\',' + reply.pReplid + ')">删除</a></li>';
            context.append(item);
            // console.log(item);
        }
        // 添加清除通知栏按钮
        var clearButton = '<li class="navbar-fixed-bottom notification-item"><a class="btn btn-success form-control" href="#" onclick="return clearReply(' + final_user_source + ',\'' + final_token + '\',' + reply.pReplid + ')">清空通知栏</a></li>';
        context.append(clearButton);
    } else {
        // 没消息，提示通知栏目为空
        context.empty();
        context.append('<li class="nav-header notification-header"><h5>通知栏</h5></li>\n');
        var noMessage = '<li class="navbar-fixed-bottom notification-item"><a class="btn btn-success form-control" href="#">无通知</a></li>';
        context.append(noMessage);
    }
}

/**
 * 通知栏状态改变
 */
var slowState = false;
var final_user_source;
var final_token;
function notificationChange(
    user_source,
    token
) {
    final_user_source = user_source;
    final_token = token;
    // 只有打开通知栏时，才更新通知栏信息
    if(slowState == false){
        // 设置消息参数
        var message_type = 0;
        var data = "";
        var has_view = this.no_view;
        var toService = this.replyService;
        // 通过ajax异步拿取消息
        sendByAjax(user_source, message_type, data, has_view, toService, notificationChangeSuccessHandler, token);
    }

    // 通知栏打开关闭动画
    var notificationBar = $('#notificationBar');
    if(notificationBar.attr('hidden') == 'hidden'){
        notificationBar.removeAttr('hidden');
        slowState = true;
    } else {
        $('#notificationBar').slideToggle('slow');
        if(slowState == true){
            slowState = false;
        } else {
            slowState = true;
        }
    }
    return false;
}

/**
 * 通知删除
 */
function replyfRm(
    user_source,
    token,
    id
) {
    alert("id --> " + id);
    // 设置消息参数
    var message_type = 1;
    var data = id + "";
    var has_view = this.no_view;
    var toService = this.replyService;
    // 通过ajax异步拿取消息
    sendByAjax(user_source, message_type, data, has_view, toService, notificationChangeSuccessHandler, token);

    return false;
}

/**
 * 清空通知栏
 */
function clearReply(
    user_source,
    token
) {
    alert("clear");
    // 设置消息参数
    var message_type = 3;
    var data = "";
    var has_view = this.no_view;
    var toService = this.replyService;
    // 通过ajax异步拿取消息
    sendByAjax(user_source, message_type, data, has_view, toService, notificationChangeSuccessHandler, token);

    return false;
}

/**
 * 申请消息管理页面
 */
function applyManView(
    user_source,
    token,
    state
) {
    // 设置消息参数
    var message_type = 0;
    var data = state + "";
    var has_view = this.yes_view;
    var toService = this.applyService;
    // 发送消息
    send(null, user_source, message_type, data, has_view, toService, token);
}

/**
 * 用户列表/管理页面
 */
function users(
    user_source,
    token,
    privilege,          /* 按权限查询 */
    onlyRoot            /* 是否只看超级用户 */
) {
    // 设置消息参数
    var message_type = 0;
    // 设置查看的用户权限（省级/校级）和 是否只查看超级用户
    var data = "[ " + privilege + "," + onlyRoot + " ]";
    var has_view = this.yes_view;
    var toService = this.userService;
    // 发送消息
    send(null, user_source, message_type, data, has_view, toService, token);
}

/**
 * 学籍列表/管理页面
 */
function studentlMan(
    user_source,
    token
) {
    // 设置消息参数
    var message_type = 0;
    // 设置查看的用户权限（省级/校级）和 是否只查看超级用户
    var data = "";
    var has_view = this.yes_view;
    var toService = this.studentService;
    // 发送消息
    send(null, user_source, message_type, data, has_view, toService, token);
}

/**
 * 专业管理；省级管理员
 */
function professionalMan(
    user_source,
    token
) {
    // 设置消息参数
    var message_type = 0;
    var data = "";
    var has_view = this.yes_view;
    var toService = this.professionalService;
    // 发送消息
    send(null, user_source, message_type, data, has_view, toService, token);
}

/**
 * 学校专业库管理
 */
function sch2proflMan(
    user_source,
    token
) {
    // 设置消息参数
    var message_type = 0;
    // 设置查看的用户权限（省级/校级）和 是否只查看超级用户
    var data = "";
    var has_view = this.yes_view;
    var toService = this.sch2profService;
    // 发送消息
    send(null, user_source, message_type, data, has_view, toService, token);
}

/**
 * 注销
 */
function logout() {
    $("#logout_form").submit();
    return true;
}

/**
 * 请求一个申请
 */
function requestApply(
    input_file,
    message,
    token
) {
    console.log("请求了一个申请...");
    // 封转申请对象
    var apply = {};
    apply.message = message;
    apply.state = 0;
    apply.result = 0;

    console.log(apply);

    // 设置消息参数
    var message_type = 1;
    var data = JSON.stringify(apply);
    var has_view = yes_view;
    var toService = applyService;
    // 发送消息
    if(input_file == null){
        send(null, message.fUserSource, message_type, data, has_view, toService, token);
    } else {
        send(input_file, message.fUserSource, message_type, data, has_view, toService, token);
    }
}

/**
 * 使用表单提交的方式发送一条消息
 */
function send(
    input_file,
    user_source,
    message_type,
    data,
    has_view,
    toService,
    // Srping Secruity需要这个值检验是否是一个合法的Post请求
    _csrf_token
) {
    var url = "/send/wv";
    // 首先，判断参数是否合法；data是可以为null的
    if(user_source != null && message_type != null && has_view !=null && toService != null && _csrf_token != null){
        // 创建一个from表单及其子元素
        var form = $("<form></form>");
        // 设置表单属性
        form.attr("action", url);
        form.attr("method", "post");
        if(input_file != null) form.attr("enctype", "multipart/form-data");
        // 设置input
        var input_token = $("<input name='_csrf' type='hidden' />");
        input_token.attr("value", _csrf_token);

        var input_user_source = $("<input type='hidden' name='fUserSource' />");
        input_user_source.attr("value", user_source);

        var input_messtype = $("<input type='hidden' name='messtype' />");
        input_messtype.attr("value", message_type);

        var input_data = $("<input type='hidden' name='data' />");
        input_data.attr("value", data);

        var input_hasView = $("<input type='hidden' name='hasView' />");
        input_hasView.attr("value", has_view);

        var input_toService = $("<input type='hidden' name='toService' />");
        input_toService.attr("value", toService);

        // input全都附加到表单里
        if(input_file != null) form.append(input_file);
        form.append(input_token);
        form.append(input_user_source);
        form.append(input_messtype);
        form.append(input_data);
        form.append(input_hasView);
        form.append(input_toService);

        // 提交表单
        form.appendTo(document.body).submit();
    }

    return false;
}

/**
 * 使用ajax发送一条消息
 */
function sendByAjax(
    user_source,
    message_type,
    data,
    has_view,
    toService,
    successHandler,
    // Srping Secruity需要这个值检验是否是一个合法的Post请求
    _csrf_token
) {
    var url = "";
    // 首先，判断参数是否合法；data是可以为null的
    if(user_source != null && message_type != null && has_view !=null && toService != null && successHandler != null && _csrf_token != null){
        // 设置url，根据是否需要视图
        if(has_view === 0)
            url = "/send/nv";
        else if (has_view === 1){
            url = "/send/wv";
        }
        // 设置请求头，放入_csrf_token
        var headers = {};
        headers['X-CSRF-TOKEN'] = _csrf_token;
        $.ajax({
            type: "POST",
            url: url,
            headers: headers,
            data: {
                fUserSource: user_source,
                messtype: message_type,
                data: data,
                hasView: has_view,
                toService: toService
            },
            //请求成功
            success : successHandler,
            //请求失败，包含具体的错误信息
            error : function(e){
                console.log(e.status);
                console.log(e.responseText);
            }
        })
    }

    return false;
}