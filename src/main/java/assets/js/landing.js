var Register_input_username;
var Register_input_password1, Register_input_password2;
var Register_field_password1, Register_field_password2;
var Register_input_fullname;
var Register_input_eula;

var Register_submit;

function Check_form() {
    var result;
    if((Register_input_password1.val() === Register_input_password2.val()) && Register_input_password1.val() != "") {
        result = false;
        Register_field_password1.removeClass("has-error");
        Register_field_password2.removeClass("has-error");
    } else {
        result = true;
        Register_field_password1.addClass("has-error");
        Register_field_password2.addClass("has-error");
    }
    
    if($.trim(Register_input_username) === "") {
        result = true;
    } else {
        result = false || result;
    }
    
    if($.trim(Register_input_fullname.val()) === "") {
        result = true;
    } else {
        result = false || result;
    }
    
    if(Register_input_eula.is(":checked")) {
        result = false || result;
    } else {
        result = true;
    }
    
    Register_submit.prop("disabled", result);
}

$(document).ready(function () {
    Register_input_username = $("#form_register .js_username input");
    
    Register_field_password1 = $("#form_register .js_password1");
    Register_field_password2 = $("#form_register .js_password2");
    
    Register_input_fullname = $("#form_register .js_fullname input");
    Register_input_password1 = $("#form_register .js_password1 input");
    Register_input_password2 = $("#form_register .js_password2 input");
    
    Register_input_eula = $("#form_register .js_checkbox input");
    
    Register_submit = $("#form_register .js_submit");
    
    Register_input_username.keydown().keyup(Check_form);
    Register_input_fullname.keyup(Check_form);
    Register_input_password1.keyup(Check_form);
    Register_input_password2.keyup(Check_form);
    Register_input_eula.change(Check_form);
});