# 神奇宝盒 NFC 捕捉应用（MengkeBox）

一个自用的小玩具应用：把手机当"闪耀流星宝盒"，把 NFC 卡片（如"歌谣萌可"）贴近手机背面感应区，就会震动+提示音+"捕捉成功"动画，并把萌可收入图鉴。

## 功能
- **捕捉模式**：贴近已登记的卡片 → 捕捉动画 + 收入图鉴
- **录入模式**：点「录入新卡片」→ 贴卡 → 输入萌可名字（如"歌谣萌可"）→ 保存。以后这张卡就可以直接捕捉了。不用提前知道卡号。

## 打包安装步骤
1. 电脑安装 Android Studio（免费）：https://developer.android.com/studio
2. 打开 Android Studio → Open → 选择本文件夹，等它自动下载 Gradle 和依赖（第一次较慢）。
3. 手机端：设置 → 关于手机 → 连点"版本号"7次开启开发者模式 → 返回 → 系统和更新 → 开发人员选项 → 打开"USB调试"。
4. 数据线连接手机，手机上弹窗"是否允许USB调试"选允许。
5. Android Studio 顶部设备选择你的 Mate X6，点绿色▶运行；或 Build → Build APK 生成安装包发到手机上安装。
6. 确保手机 NFC 开关已打开（设置里搜"NFC"）。Mate X6 的 NFC 感应区在背面摄像头附近。

## 重要提醒（华为用户）
如果手机系统已升级到 **HarmonyOS NEXT（纯血鸿蒙，不再兼容安卓应用）**，APK 将无法安装，需要改用鸿蒙原生开发（ArkTS）。Mate X6 出厂是兼容安卓应用的 HarmonyOS 4.x，未升级纯血鸿蒙即可正常使用本应用。

## 已知限制
- 图标、音效用的是系统默认资源，可自行替换 res/ 下的资源文件。
- 不同批次玩具卡的 NFC 芯片类型可能不同；本应用用卡号(UID)识别，NFC-A/B/F/V 都支持，理论上通用。

## 免安装开发环境：GitHub 云编译 APK（推荐）
不想装 Android Studio 的话，用 GitHub 免费云端编译，全程网页操作：

1. 注册 github.com 账号（免费）。
2. 网页右上角 "+" → New repository → 名字填 `mengkebox` → 选 Private → Create。
3. 进入仓库后点 "uploading an existing file" 或 Add file → Upload files，
   把本文件夹（含 `.github` 隐藏文件夹）里的全部内容拖上去。注意要全选：
   settings.gradle、build.gradle、gradle.properties、app 文件夹、.github 文件夹。
4. 上传完成后，点上方 **Actions** 标签页 → 左侧选 "Build APK" → 右侧 "Run workflow" → Run。
5. 等几分钟（页面上的黄点变绿勾）→ 点进那次运行记录 → 最下方 Artifacts 里的
   **MengkeBox-apk** 就是安装包，点它下载。
6. 把下载到的 app-debug.apk 传到手机（微信文件传输、QQ、邮件附件均可），
   手机上点击安装，如提示"禁止安装未知应用"按提示允许一次即可。
7. 使用前确认手机 NFC 开关已打开。

整个过程不需要在电脑上装任何东西。
