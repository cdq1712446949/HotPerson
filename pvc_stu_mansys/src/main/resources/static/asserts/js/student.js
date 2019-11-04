/**
 * 学籍添加页面
 */
function stuAddView(
    user_source,
    token
) {
    // 设置消息参数
    var message_type = 1;
    var data = "";
    var has_view = yes_view;
    var toService = studentService;
    // 发送消息
    send(null, user_source, message_type, data, has_view, toService, token);

    return false;
}

/**
 * 学籍查看页面，也是编辑页面，二合一
 */
function stuDescView(
    user_source,
    token,
    id
) {
    // 设置消息参数
    var message_type = 1;
    var data = id + "";
    var has_view = yes_view;
    var toService = studentService;
    // 发送消息
    send(null, user_source, message_type, data, has_view, toService, token);

    return false;
}

/**
 * 学籍删除
 */
function stuRm(
    user_source,
    token,
    id
) {
    var b1 = confirm("数据无价，你想好了？");
    if(b1){
        var b2 = confirm("学籍是重要资料，一般情况下都不会删除，我们相信您作为省级的管理员，一定已经明白其重要性，所以，请确认您的操作！");
        if(b2){
            // 设置消息参数
            var message_type = 3;
            var data = id + "";
            var has_view = yes_view;
            var toService = studentService;
            // 发送消息
            send(null, user_source, message_type, data, has_view, toService, token);
        }
    }
    return false;
}

/**
 * 学籍添加/编辑申请
 */
function studentAddRequestApply(
    user_source,
    token
) {
    // 获取头像文件input
    var input_file = document.getElementById("head_icon_file_input");
    var b = confirm("申请一旦提交，就无法更改或撤销！");
    if(b){
        // 封装学籍对象
        var student = {};
        student.pStuid = $("input[name = 'pStuid']").val();
        student.code = $("input[name = 'code']").val();
        student.name = $("input[name = 'name']").val();
        student.idnumber = $("input[name = 'idnumber']").val();
        student.homeadd = $("input[name = 'homeadd']").val();
        student.phone = $("input[name = 'phone']").val();
        student.fSchool = $("input[name = 'fSchool']").val();
        student.enrolltime = $("input[name = 'enrolltime']").val();
        student.schoYearSys = $("input[name = 'schoYearSys']").val();
        student.graduatime = $("input[name = 'graduatime']").val();
        student.diploma = parseInt($('#prof_select').val());
        student.fProfessional = parseInt($('#stu_prof_select').val());

        // 封装申请的消息对象
        var message = {};
        message.fUserSource = user_source;
        message.messtype = 2;
        message.data = JSON.stringify(student);
        message.hasView = no_view;
        message.toService = studentService;

        // 请求申请
        requestApply(input_file, message, token);
    }

    return false;
}

/**
 * 学生删除申请
 */
function studentRmRequestApply(
    user_source,
    token,
    id
) {
    var b = confirm("申请一旦提交，就无法更改或撤销！");
    if(b){
        // 封装申请的消息对象
        var message = {};
        message.fUserSource = user_source;
        message.messtype = 3;
        message.data = id + "";
        message.hasView = no_view;
        message.toService = studentService;

        // 请求申请
        requestApply(null, message, token);
    }
}
