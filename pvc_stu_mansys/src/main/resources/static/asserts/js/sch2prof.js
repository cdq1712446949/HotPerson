/**
 * 学校-专业 添加页面
 * 作者：辰湖飞彦
 */
function sch2profAddView(
    user_source,
    token
) {
    // 设置消息参数
    var message_type = 1;
    var data = "";
    var has_view = 1;
    var toService = sch2profService;
    // 发送消息
    send(null, user_source, message_type, data, has_view, toService, token);

    return false;
}

/**
 * 学校-专业 编辑页面
 * 作者：辰湖飞彦
 */
function sch2profEditView(
    user_source,
    token,
    id
) {
    // 设置消息参数
    var message_type = 1;
    var data = id + "";
    var has_view = 1;
    var toService = sch2profService;
    // 发送消息
    send(null, user_source, message_type, data, has_view, toService, token);

    return false;
}

/**
 * 学校-专业映射 添加或更新，省级
 * 作者：辰湖飞彦
 */
function sch2profSaveOrUpdateBySheng(
    user_source,
    token
) {
    // 封装 学校-专业映射 json对象
    var sch2prof = {};
    sch2prof.pSchProfId = $("input[name = 'pSchProfId']").val();
    sch2prof.fSchool = parseInt($('#school_select').val());
    sch2prof.fProfessional = parseInt($('#sch2prof_prof_select').val());

    console.log(sch2prof);

    // 设置消息参数
    var message_type = 2;
    var data = JSON.stringify(sch2prof);
    var has_view = yes_view;
    var toService = sch2profService;
    // 发送消息
    send(null, user_source, message_type, data, has_view, toService, token);

    return false;
}

/**
 * 学校-专业映射 添加或更新，校级
 * 作者：辰湖飞彦
 */
function sch2profSaveOrUpdateByXiao(
    user_source,
    token
) {
    // 封装 学校-专业映射 json对象
    var sch2prof = {};
    sch2prof.pSchProfId = $("input[name = 'pSchProfId']").val();
    sch2prof.fSchool = parseInt($("input[name = 'fSchool']").val());
    sch2prof.fProfessional = parseInt($('#sch2prof_prof_select').val());

    console.log(sch2prof);

    // 设置消息参数
    var message_type = 2;
    var data = JSON.stringify(sch2prof);
    var has_view = yes_view;
    var toService = sch2profService;
    // 发送消息
    send(null, user_source, message_type, data, has_view, toService, token);

    return false;
}

/**
 * 学校-专业删除
 * 作者：辰湖飞彦
 */
function sch2profRm(
    user_source,
    token,
    id
) {
    // 询问用户确认
    var b = confirm("数据无价，你想好了？");
    if(b){
        // 设置消息参数
        var message_type = 3;
        var data = id + "";
        var has_view = 1;
        var toService = sch2profService;
        // 发送消息
        send(null, user_source, message_type, data, has_view, toService, token);
    }

    return false;
}

/**
 * 没有学校所需专业，处理，跳到添加专业申请页面
 */
function noProfView(
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