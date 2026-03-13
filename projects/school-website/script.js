// 表单验证函数
function validateForm() {
    var username = document.getElementById('username').value;
    var password = document.getElementById('password').value;
    var confirmPassword = document.getElementById('confirmPassword').value;
    
    // 检查用户名是否为空
    if (username === '' || username.trim() === '') {
        alert('用户名不能为空！');
        return false;
    }
    
    // 检查密码长度
    if (password.length < 6) {
        alert('密码长度不能少于6位！');
        return false;
    }
    
    // 检查两次密码是否一致
    if (password !== confirmPassword) {
        alert('两次输入的密码不一致！');
        return false;
    }
    
    // 验证通过
    alert('注册成功！');
    return true;
}

// 导航栏滚动效果
window.addEventListener('scroll', function() {
    var navbar = document.querySelector('.navbar-custom');
    if (navbar) {
        if (window.scrollY > 50) {
            navbar.style.backgroundColor = 'red';
            navbar.style.boxShadow = '0 20px 10px blue';
        } else {
            navbar.style.backgroundColor = '#333';
            navbar.style.boxShadow = 'none';
        }
    }
});