# git 整理的内容（结合案例随便写写）

## git  add 从暂存区删除文件

在 git 中 将文件添加到暂存区的命令是： git add 

之后文件会被从工作区添加到暂存区

可以通过 `git status ` 命令查看 git 仓库当前的状态 

同样的 可以通过 `git rm --cached 文件名`的方法 删除暂存区中的文件 （将文件从暂存区中移除，不会影响工作区的文件内容）

> 流程示例如下：

```shell
# 在对应目录中创建 git 测试文件
$ vi test01
# 此时通过命令查看暂存区的状态
$ git status
On branch master
Your branch is up to date with 'origin/master'.

Untracked files:
  (use "git add <file>..." to include in what will be committed)
        test01
# 将对应的文件添加到暂存区中
$ git add test01
warning: LF will be replaced by CRLF in git补充/test01.
The file will have its original line endings in your working directory
# 查看暂存区文件状态（新增测试文件）
$ git status
On branch master
Your branch is up to date with 'origin/master'.

Changes to be committed:
  (use "git restore --staged <file>..." to unstage)
        new file:   test01
# 在暂存区中删除测试文件
$ git rm --cached test01
rm 'git补充/test01'
# 再次查看暂存区状态（测试文件从暂存区中被移除）
$ git status
On branch master
Your branch is up to date with 'origin/master'.

Untracked files:
  (use "git add <file>..." to include in what will be committed)
        test01
# 检查工作区，工作区文件未受影响

```

## GIT 相关 bug

###  提交代码出现错误代码：10054

* 问题描述：

将代码推向指定仓库时出现的错误：

> fatal: unable to access 'https://github.com/MartinFJ13713/work_as_a_java_developer.git/': OpenSSL SSL_read: Connection was reset, errno 10054

原因 登录 github 存在权限校验问题 

解决方法，提交代码之前，在git bash 命令行中 加入一个参数设置

```bash
$ git config --global http.sslVerify "false"
```

关闭 代码提交的权限检查

在idea中在 github 相关设置中添加当前用户账户即可

<img src="https://img-blog.csdnimg.cn/20210520220746363.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3RnOTI4NjAwNzc0,size_16,color_FFFFFF,t_70" alt="img" style="zoom:67%;" />

之后 通过浏览器登录jetbrains设置对应的 git 账户

<img src="https://img-blog.csdnimg.cn/20210520214601505.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3RnOTI4NjAwNzc0,size_16,color_FFFFFF,t_70" alt="img" style="zoom:67%;" />

### 当前提交的代码版本落后于仓库中的版本 

* 问题描述 

使用命令 `git reset  --hard `回退到某个版本之后重新向仓库中提交内容 （让回退的版本变成最新的版本）在代码远程提交的时候出现问题：

> To https://github.com/MartinFJ13713/work_as_a_java_developer.git
>  ! [rejected]        master -> master (non-fast-forward)
> error: failed to push some refs to 'https://github.com/MartinFJ13713/work_as_a_java_developer.git'
> hint: Updates were rejected because the tip of your current branch is behind

应该是git 在当前工作空间中负责控制版本的指针与仓库中的不一致 ，代码中有冲突的内容 `需要强行 merge`
解决方案：使用本地的内容强行替换代码仓库中的内容

使用命令：

```bash 
$ git push -u origin master -f
```

将当前 暂存区中的内容提交并覆盖远程仓库的内容

代码参考：[hint: Updates were rejected because the tip of your current branch is behind(github上的版本和本地版本冲突的解决方法)_NPException的博客-CSDN博客](https://blog.csdn.net/qq_36850813/article/details/86738720)

