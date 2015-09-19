var Register_password1, Register_password2;
var Register_submit;
var Register_fullname;

function Check_form() {
    var result;
    if(Register_password1.val() === Register_password2.val()) {
        result = false;
    } else {
        result = true;
    }
    
    if($.trim(Register_fullname.val()) === "") {
        result = true;
    } else {
        result = false || result;
    }
    
    Register_submit.prop("disabled", result);
}

$(document).ready(function () {
    Register_fullname = $("#form_register .js_fullname");
    
    Register_password1 = $("#form_register .js_password1");
    Register_password2 = $("#form_register .js_password2");
    
    Register_submit = $("#form_register .js_submit");
    
    Register_fullname.keyup(Check_form);
    Register_password1.keyup(Check_form);
    Register_password2.keyup(Check_form);
});