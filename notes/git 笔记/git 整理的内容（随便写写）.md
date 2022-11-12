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

