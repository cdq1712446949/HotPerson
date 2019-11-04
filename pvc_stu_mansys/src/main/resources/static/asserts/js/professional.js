
/**
 * 专业添加页面
 */
function profAddView(
    user_source,
    token
) {
    // 设置消息参数
    var message_type = 1;
    var data = "";
    var has_view = yes_view;
    var toService = professionalService;
    // 发送消息
    send(null, user_source, message_type, data, has_view, toService, token);

    return false;
}

/**
 * 专业编辑页面
 */
function profEditView(
    user_source,
    token,
    profId
) {
    // 设置消息参数
    var message_type = 1;
    var data = profId + "";
    var has_view = yes_view;
    var toService = professionalService;
    // 发送消息
    send(null, user_source, message_type, data, has_view, toService, token);

    return false;
}

/**
 * 专业删除页面
 */
function profRm(
    user_source,
    token,
    profId
) {
    var b = confirm("数据无价，你想好了？");
    if(b){
        // 设置消息参数
        var message_type = 3;
        var data = profId + "";
        var has_view = yes_view;
        var toService = professionalService;
        // 发送消息
        send(null, user_source, message_type, data, has_view, toService, token);
    }

    return false;
}

/**
 * 专业添加/更新
 */
function profSaveOrUpdate(
    user_source,
    token
) {
    // 封装专业json对象
    var professional = {};
    professional.pProfid = $("input[name = 'pProfid']").val();
    professional.name = $("input[name = 'name']").val();
    professional.fProfcategory = parseInt($('#profcategory_select').val());

    console.log(professional);

    // 设置消息参数
    var message_type = 2;
    var data = JSON.stringify(professional);              /* 将user数据放入消息data中 */
    var has_view = yes_view;
    var toService = professionalService;
    // 发送消息
    send(null, user_source, message_type, data, has_view, toService, token);

    return false;
}

/**
 * 请求一个专业添加申请
 */
function profAddRequestApply(
    user_source,
    token
) {
    var b = confirm("申请一旦提交，就无法更改或撤销！");
    if(b){
        // 封装专业对象
        var professional = {};
        professional.pProfid = $("input[name = 'pProfid']").val();
        professional.name = $("input[name = 'name']").val();
        professional.fProfcategory = parseInt($('#profcategory_select').val());

        // 封装申请的消息对象
        var message = {};
        message.fUserSource = user_source;
        message.messtype = 2;
        message.data = JSON.stringify(professional);
        message.hasView = no_view;
        message.toService = professionalService;

        // 请求申请
        requestApply(null, message, token);
    }

    return false;
}

