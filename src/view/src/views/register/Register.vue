<template>
  <div class="register-container">
    <!-- 注册面板 -->
    <div class="register-panel">
      <!-- 左侧图片区域 -->
      <div class="illustration">
        <img src="/bag.png" alt="健康生活" class="illustration-img" />
      </div>

      <!-- 右侧注册表单 -->
      <div class="right-register">
        <div class="register-header">
          <h2>注册账户</h2>
          <p class="welcome-text">健康生活，从此刻开始</p>
        </div>

        <div class="input-group">
          <input v-model="account" class="register-input" placeholder="输入账号" />
        </div>

        <div class="input-group">
          <input v-model="username" class="register-input" placeholder="用户名" />
        </div>

        <div class="input-group">
          <input v-model="password" class="register-input" type="password" placeholder="输入密码" />
        </div>

        <div class="input-group">
          <input v-model="againPassword" class="register-input" type="password" placeholder="确认密码" />
        </div>

        <div class="button-group">
          <button class="register-btn" @click="registerFunc">
            <span class="btn-text">立即注册</span>
          </button>
        </div>

        <div class="register-footer">
          <p>已有账户？<span class="login-link" @click="toDoLogin">返回登录</span></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Register",
  data() {
    return {
      account: '', // 用户账号
      password: '', // 用户密码
      againPassword: '', // 用户确认密码
      username: '' // 用户名
    }
  },
  methods: {
    // 返回登录页面
    toDoLogin() {
      this.$router.push('/login');
    },

    async registerFunc() {
      if (!this.account || !this.password || !this.againPassword || !this.username) {
        this.$message.info('请填写相关信息哦!');
        return;
      }

      if (this.password !== this.againPassword) {
        this.$message.info('前后密码输入不一致!');
        return;
      }

      // 密码二次md5加密
      const bcryptPassword = this.$md5(this.$md5(this.password));

      // 构建注册参数UserRegisterDto类
      const userRegisterDto = {
        account: this.account,
        password: bcryptPassword,
        username: this.username
      }

      try {
        const { message } = await this.$axios.post(`user/register`, userRegisterDto);
        this.$message.success(message);
        this.$router.go(-1); // 返回登录页
      } catch (error) {
        this.$message.error(error.message);
      }
    }
  }
};
</script>

<style lang="scss" scoped>
* {
  user-select: none;
  box-sizing: border-box;
  font-family: 'Poppins', 'Segoe UI', sans-serif;
}

.register-container {
  width: 100%;
  min-height: 100vh;
  background-color: #fafafa;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;

  .register-panel {
    display: flex;
    border-radius: 16px;
    height: auto;
    background-color: white;
    box-shadow: 0 10px 20px rgba(0, 100, 80, 0.1);
    overflow: hidden;
    max-width: 800px;
    width: 100%;

    .illustration {
      width: 50%;
      padding: 30px;
      display: flex;
      justify-content: center;
      align-items: center;
      background-color: rgb(73, 204, 115);

      .illustration-img {
        width: 100%;
        max-width: 300px;
      }
    }

    .right-register {
      width: 50%;
      background-color: white;
      padding: 30px;
      display: flex;
      flex-direction: column;
      justify-content: center;

      .register-header {
        margin-bottom: 30px;

        h2 {
          color: rgb(10, 107, 41);
          font-size: 28px;
          font-weight: 600;
          margin-bottom: 8px;
        }

        .welcome-text {
          color: #272c33;
          font-size: 14px;
          font-weight: 400;
        }
      }

      .input-group {
        margin-bottom: 15px;

        .register-input {
          width: 100%;
          height: 50px;
          font-size: 22px;
          padding: 0 15px;
          background-color: #f8fafc;
          border: none;
          border-radius: 8px;
          font-weight: 600;
          color: #2d3748;

          &:focus {
            outline: none;
            background-color: #f1f3f6;
          }

          &::placeholder {
            color: #a0aec0;
            font-weight: 400;
          }
        }
      }

      .button-group {
        margin-top: 30px;

        .register-btn {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 100%;
          height: 50px;
          background-color: rgb(71, 202, 112);
          color: white;
          border: none;
          border-radius: 8px;
          font-size: 16px;
          font-weight: 500;
          cursor: pointer;
          padding: 0 25px;

          &:hover {
            background-color: rgb(66, 191, 105);
          }
        }
      }

      .register-footer {
        margin-top: 20px;
        text-align: right;

        p {
          color: #718096;
          font-size: 14px;

          .login-link {
            color: #2d6a4f;
            font-weight: 500;
            cursor: pointer;

            &:hover {
              text-decoration: underline;
            }
          }
        }
      }
    }
  }
}
</style>