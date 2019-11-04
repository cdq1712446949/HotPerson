/**
 * 当 只查看超级用户 复选框状态改变时
 */
function onlyRootChange() {
    var checked = $("#onlyRoot_checkbox:checked").val();

    var sheng_privilege_a  = $('#sheng_privilege_a');
    var xiao_privilege_a = $('#xiao_privilege_a');

    if(checked == "on"){
        // 被选中
        // 设置点击属性
        sheng_privilege_a.attr("onclick", "return users( $('#user_source').val(), $('#token').val(), 0, 1 )");
        xiao_privilege_a.attr("onclick", "return users( $('#user_source').val(), $('#token').val(), 1, 1 )");
    } else {
        // 没被选中
        sheng_privilege_a.attr("onclick", "return users( $('#user_source').val(), $('#token').val(), 0, 0 )");
        xiao_privilege_a.attr("onclick", "return users( $('#user_source').val(), $('#token').val(), 1, 0 )");
    }

    return false;
}

/**
 * 用户添加 页面
 */
function userAddView(
    user_source,
    token
) {
    // 设置消息参数
    var message_type = 1;
    var data = "";
    var has_view = 1;
    var toService = userService;
    // 发送消息
    send(null, user_source, message_type, data, has_view, toService, token);

    return false;
}

/**
 * 用户编辑 页面
 */
function userEditView(user_source, token, uid) {
    // 设置消息参数
    var message_type = 1;
    // 编辑页面需要用户id
    var data = uid + "";
    var has_view = 1;
    var toService = userService;
    // 发送消息
    send(null, user_source, message_type, data, has_view, toService, token);

    return false;
}

/**
 * 用户删除
 */
function userRm(user_source, token, uid) {
    // 询问用户确认
    var b = confirm("数据无价，你想好了？");
    if(b){
        // 设置消息参数
        var message_type = 3;
        // 删除用户需要用户id
        var data = uid + "";
        var has_view = 1;
        var toService = userService;
        // 发送消息
        send(null, user_source, message_type, data, has_view, toService, token);
    }

    return false;
}

/**
 * 超级用户--用户添加/更新
 */
function RootUserSaveOrUpdate(user_source, token) {
    // 将User数据封装成一个json对象
    var user = {};
    user.pUid = $("input[name = 'pUid']").val();
    user.name = $("input[name = 'name']").val();
    user.pwd = $("input[name = 'pwd']").val();
    user.role = parseInt($("input[name = 'role']:checked").val());
    if(user.role == 0){
        user.privilege = 1;
    } else {
        user.privilege = parseInt($("input[name = 'privilege']:checked").val());
    }

    var uinfo = {};
    uinfo.position = $("input[name = 'uInfo.position']").val();
    uinfo.phone = $("input[name = 'uInfo.phone']").val();
    if(user.privilege == 1){
        uinfo.fSchool = parseInt($('#school_select').val());
    }
    user.uinfo = uinfo;
    console.log(user);
    // 设置消息参数
    var message_type = 2;
    var data = JSON.stringify(user);              /* 将user数据放入消息data中 */
    var has_view = 1;
    var toService = userService;
    // 发送消息
    send(null, user_source, message_type, data, has_view, toService, token);

    return false;
}

/**
 * 校级超级用户--用户添加或更新
 */
function userSaveOrUpdate(user_source, token) {
    // 将User数据封装成一个json对象
    var user = {};
    user.pUid = $("input[name = 'pUid']").val();
    user.name = $("input[name = 'name']").val();
    user.pwd = $("input[name = 'pwd']").val();
    user.role = 1;
    user.privilege = 1;

    var uinfo = {};
    uinfo.position = $("input[name = 'uInfo.position']").val();
    uinfo.phone = $("input[name = 'uInfo.phone']").val();
    uinfo.fSchool = $("input[name = 'uinfo.fSchool']").val();

    user.uinfo = uinfo;
    console.log(user);
    // 设置消息参数
    var message_type = 2;
    var data = JSON.stringify(user);              /* 将user数据放入消息data中 */
    var has_view = 1;
    var toService = userService;
    // 发送消息
    send(null, user_source, message_type, data, has_view, toService, token);

    return false;
}