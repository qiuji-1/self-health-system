# Git SSH 配置完整总结

## 一、问题背景

**项目信息**：
- 项目名称：SpringBoot+Vue 的个人健康系统
- Git 远程地址：`git@github.com:qiuji-1/self-health-system.git`
- 邮箱：2731226814@qq.com

**目标**：配置 Git SSH 密钥以实现免密推送代码到 GitHub 仓库

---

## 二、执行步骤

### 1. 生成 SSH 密钥对

**操作**：使用 Ed25519 算法生成密钥
```bash
ssh-keygen -t ed25519 -C "2731226814@qq.com"
```

**描述**：在用户目录下 `C:/Users/liangjiahao/.ssh/` 下生成两个文件：
- `id_ed25519` - 私钥文件，需要保密
- `id_ed25519.pub` - 公钥文件，用于添加到 GitHub

**备注**：公钥已备份到项目目录 `god key/github_ssh_pubkey.txt`

---

### 2. 启动 ssh-agent 服务

**操作**：配置服务为自动启动并启动服务
```bash
Set-Service ssh-agent -StartupType Automatic
Start-Service ssh-agent
```

**描述**：
- `Set-Service ssh-agent -StartupType Automatic`：将 ssh-agent 服务设置为自动启动，确保系统重启后服务会自动运行
- `Start-Service ssh-agent`：立即启动 ssh-agent 服务

**注意**：需要管理员权限执行

---

### 3. 解决 hosts 文件冲突

**操作**：注释 hosts 文件中的 Steam++ 修改并刷新 DNS

**文件位置**：`C:/Windows/System32/drivers/etc/hosts`

**修改前**（第 44 行）：
```
127.0.0.1 github.com
```

**修改后**（第 44 行）：
```
#127.0.0.1 github.com
```

**刷新 DNS 缓存**：
```bash
ipconfig /flushdns
```

**描述**：
- Steam++ 软件修改了系统的 hosts 文件，将 `github.com` 解析到本地地址 `127.0.0.1`
- 这导致 SSH 连接时无法正确连接到 GitHub 服务器
- 通过注释掉该行并刷新 DNS 缓存，恢复正常的域名解析

---

### 4. 添加公钥到 GitHub

**操作**：将公钥内容添加到 GitHub SSH Keys 设置

**步骤**：
1. 复制 `god key/github_ssh_pubkey.txt` 中的公钥内容
2. 登录 GitHub 账户
3. 进入 Settings → SSH and GPG keys
4. 点击 "New SSH key"
5. 粘贴公钥内容，保存

**描述**：
- 公钥添加到 GitHub 账户后，当使用 SSH 连接 GitHub 时，GitHub 可以验证你的身份
- 这样就不需要每次推送代码时输入密码
- 一个公钥可以对应多个项目

---

### 5. 测试 SSH 连接

**操作**：测试 SSH 连接是否成功
```bash
ssh -T git@github.com
```

**首次连接会提示**：
```
The authenticity of host 'github.com (140.82.114.3)' can't be established.
ED25519 key fingerprint is SHA256:...
Are you sure you want to continue connecting (yes/no)?
```

**输入**：`yes`

**成功响应**：
```
Hi qiuji-1! You've successfully authenticated, but GitHub does not provide shell access.
```

**描述**：
- 首次连接需要确认 GitHub 服务器的真实性
- 输入 `yes` 后会将 GitHub 的服务器指纹添加到已知主机列表
- 看到 `Hi qiuji-1!` 消息表示 SSH 配置成功
- `does not provide shell access` 是正常的，GitHub 不提供 shell 访问

---

## 三、遇到的错误及解决方案

### 错误 1：ssh-agent 无法启动

**错误现象**：
```
ssh-add: error connecting to agent: no such file or directory
```

**原因分析**：
- ssh-agent 服务未启动
- 服务未配置为自动启动

**解决方案**：
```bash
# 以管理员身份运行 PowerShell，执行以下命令：
Set-Service ssh-agent -StartupType Automatic
Start-Service ssh-agent
```

**结果**：服务启动成功，后续的 ssh-add 命令可以正常执行

---

### 错误 2：github.com 解析到 127.0.0.1

**错误现象**：
```bash
ssh -T git@github.com
# 提示连接被拒绝或超时
```

**原因分析**：
- 检查发现 `ping github.com` 返回 `127.0.0.1`
- 查看 hosts 文件发现第 44 行有 `127.0.0.1 github.com`
- 这是 Steam++ 软件的修改，用于加速 GitHub 访问（但此时导致问题）

**解决方案**：
1. 注释掉 hosts 文件中的该行：
   ```
   #127.0.0.1 github.com
   ```
2. 刷新 DNS 缓存：
   ```bash
   ipconfig /flushdns
   ```

**结果**：`ping github.com` 返回正确的 IP 地址，SSH 连接可以正常进行

---

### 错误 3：Permission denied (publickey)

**错误现象**：
```bash
ssh -T git@github.com
git@github.com: Permission denied (publickey).
```

**原因分析**：
- 虽然已经生成了 SSH 密钥对
- 但公钥还未添加到 GitHub 账户
- GitHub 无法验证用户身份

**解决方案**：
1. 打开 `god key/github_ssh_pubkey.txt`，复制公钥内容
2. 登录 GitHub，进入 Settings → SSH and GPG keys
3. 点击 "New SSH key"，粘贴公钥内容
4. 保存后重新测试连接

**结果**：SSH 连接成功，显示欢迎信息

---

## 四、最终验证

**测试命令**：
```bash
ssh -T git@github.com
```

**成功输出**：
```
Hi qiuji-1! You've successfully authenticated, but GitHub does not provide shell access.
```

**验证结果**：
- ✅ SSH 密钥对生成成功
- ✅ ssh-agent 服务正常运行
- ✅ hosts 文件冲突已解决
- ✅ 公钥已添加到 GitHub 账户
- ✅ SSH 连接测试通过

---

## 五、常用 Git SSH 命令

```bash
# 生成 SSH 密钥对
ssh-keygen -t ed25519 -C "your_email@example.com"

# 启动 ssh-agent 服务
Start-Service ssh-agent

# 添加私钥到 ssh-agent
ssh-add ~/.ssh/id_ed25519

# 测试 SSH 连接
ssh -T git@github.com

# 查看公钥内容
cat ~/.ssh/id_ed25519.pub

# 列出已添加的密钥
ssh-add -l
```

---

## 六、相关文件位置

| 文件 | 位置 | 说明 |
|------|------|------|
| SSH 私钥 | `C:/Users/liangjiahao/.ssh/id_ed25519` | 私钥文件，需保密 |
| SSH 公钥 | `C:/Users/liangjiahao/.ssh/id_ed25519.pub` | 公钥文件 |
| 公钥备份 | `god key/github_ssh_pubkey.txt` | 公钥备份文件 |
| Hosts 文件 | `C:/Windows/System32/drivers/etc/hosts` | 系统 DNS 解析配置 |

---

## 七、注意事项

1. **私钥安全**：私钥文件 `id_ed25519` 必须保密，不要分享给他人
2. **权限问题**：启动 ssh-agent 服务需要管理员权限
3. **Hosts 文件**：修改 hosts 文件需要管理员权限
4. **Steam++ 冲突**：使用 Steam++ 等 GitHub 加速工具时，如果遇到连接问题，检查 hosts 文件配置
5. **多账户**：如果需要在同一台机器上使用多个 GitHub 账户，需要配置不同的 SSH 密钥并使用 SSH config 文件

---

## 八、总结

通过以上步骤，成功配置了 Git SSH 免密推送功能。主要解决了：
- ssh-agent 服务启动问题
- hosts 文件被 Steam++ 修改导致的 DNS 解析冲突
- 公钥未添加到 GitHub 账户的身份验证问题

配置完成后，可以正常使用 `git push` 和 `git pull` 命令，无需输入密码。

---

**配置日期**：2026-03-30
**配置人**：梁家豪
