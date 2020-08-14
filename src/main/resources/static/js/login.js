const username = document.querySelector('.username');
const password = document.querySelector('.password');
const captchaInput = document.querySelector('.captcha-input');

const btnLoading = document.querySelector('.btn-loading');
const submitBtn = document.querySelector('.submit-btn');

const usernameError = document.querySelector('.username-error');
const passwordError = document.querySelector('.password-error');
const captchaInputError = document.querySelector('.captcha-error');

window.onload = function () {
    if (usernameError.innerHTML !== '') {
        username.className = 'username username-input-error';
        usernameError.className = 'username-error opacity1';
    }
};

function handleSubmit(e) {
    if (username.value === '') {
        username.className = 'username username-input-error';
        usernameError.className = 'username-error opacity1';
        usernameError.innerHTML = '请输入用户名';
        return false;
    }
    if (password.value === '') {
        passwordError.className = 'password-error opacity1';
        usernameError.innerHTML = '请输入密码';
        return false
    }
    if (captchaInput.value === '') {
        // 验证码的css样式还没写
        captchaInputError.className = 'captcha-error opacity1';
        captchaInputError.innerHTML = '请输入验证码';
        return false
    }
    btnLoading.style.display = 'block';
    submitBtn.style.backgroundColor = '#b3b3b3';
    submitBtn.style.cursor = 'default';
    return true;
}

function usernameInputFocus() {
    if (username.className === 'username username-input-error') {
        username.className = 'username username-input-default';
        usernameError.className = 'username-error opacity0';
    }
}

function passwordInputFocus() {
    if (password.className === 'password password-input-error') {
        password.className = 'password password-input-default';
        passwordError.className = 'password-error opacity0';
    }
}

function changeCaptcha() {
    let captchaImg = document.querySelector('.captcha-img');
    captchaImg.src = "/sso/captcha?" + new Date().getTime();
}
