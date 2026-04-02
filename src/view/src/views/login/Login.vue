<template>
  <div class="login-container">
    <!-- 登录面板 -->
    <div class="login-panel">
      <!-- 左侧图片区域 -->
      <div class="illustration">
        <img src="/bag.png" alt="健康生活" class="illustration-img" />
      </div>

      <!-- 右侧登录表单 -->
      <div class="right-login">
        <div class="login-header">
          <h2>个人健康系统</h2>
          <p class="welcome-text">记录健康，享受生活</p>
        </div>

        <div class="input-group">
          <input v-model="account" class="login-input" placeholder="输入账号" />
        </div>

        <div class="input-group">
          <input v-model="password" class="login-input" type="password" placeholder="输入密码" />
        </div>

        <div class="button-group">
          <button class="login-btn" @click="login">
            <span class="btn-text">立即登录</span>
          </button>
        </div>

        <div class="login-footer">
          <p>没有账号？<span class="register-link" @click="toDoRegister">点此注册</span></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { setToken, setRole } from "@/utils/storage.js";

export default {
  name: "Login",
  data() {
    return {
      account: '',
      password: ''
    }
  },
  methods: {
    toDoRegister() {
      this.$router.push('/register');
    },

    async login() {
      if (!this.account || !this.password) {
        this.$message.info('账号与密码不能为空哦');
        return;
      }

      const bcryptPassword = this.$md5(this.$md5(this.password));
      const userLoginDto = { account: this.account, password: bcryptPassword };
      try {
        const { data, message } = await this.$axios.post(`user/login`, userLoginDto);
        setToken(data.token);
        setRole(data.role);
        if (data.role === 1) {
          this.$router.push('/admin');
        } else if (data.role === 2) {
          this.$router.push('/user');
        }
      } catch (error) {
        console.error('登录异常:', error);
        this.$message.error(error.message);
      }
    },
  }
};
</script>

<style lang="scss" scoped>
* {
  user-select: none;
  box-sizing: border-box;
  font-family: 'Poppins', 'Segoe UI', sans-serif;
}

.login-container {
  width: 100%;
  min-height: 100vh;
  background-color: #fafafa;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;

  .login-panel {
    display: flex;
    border-radius: 16px;
    height: auto;
    background-color: #fff;
    box-shadow: 0 10px 20px rgba(0, 100, 80, 0.1);
    overflow: hidden;
    max-width: 800px;
    width: 100%;

    .illustration {
      width: 50%;
      padding: 20px;
      display: flex;
      justify-content: center;
      align-items: center;
      background-color: rgb(71, 202, 112);

      .illustration-img {
        width: 100%;
        max-width: 300px;
      }
    }

    .right-login {
      width: 50%;
      background-color: white;
      padding: 30px;
      display: flex;
      flex-direction: column;
      justify-content: center;

      .login-header {
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
        margin-bottom: 25px;

        .login-input {
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

        .login-btn {
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

      .login-footer {
        margin-top: 20px;
        text-align: right;

        p {
          color: #718096;
          font-size: 14px;

          .register-link {
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