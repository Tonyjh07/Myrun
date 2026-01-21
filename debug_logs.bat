@echo off
echo 正在连接设备并查看日志...
echo 使用 Ctrl+C 停止日志输出
echo.

"C:\Users\yjh07\AppData\Local\Android\Sdk\platform-tools\adb.exe" logcat -d | findstr "AIConfigManager\|SettingsActivity\|AIActivity"
echo.
echo 实时日志监控中...
"C:\Users\yjh07\AppData\Local\Android\Sdk\platform-tools\adb.exe" logcat | findstr "AIConfigManager\|SettingsActivity\|AIActivity"