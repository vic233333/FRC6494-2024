**<ins>注意：</ins> 虽然这段代码旨在为COTS模块提供即插即用的配置/调整参数，但有几个配置/调整未经过Phoenix 6的更改进行测试。如果该代码用于这些未经测试的模块之一，则模块特定的配置和调整很可能需要编辑。**

**如果有人愿意贡献配置/调整以添加为“已测试”，请发送消息或提出PR。**

**向下滚动查看已测试/未测试配置的完整列表。**

# BaseTalonFXSwerve </br>

**使用TalonFX控制器、CTRE CANCoder和CTRE Pigeon Gyro的万向轮模块的基本万向轮代码** </br>
此代码是为Swerve Drive Specialties的MK3、MK4、MK4i和WCP SwerveX风格的模块设计的，但应该可以轻松适应其他风格的模块。</br>

**设置常量**
----
必须在Constants.java文件中调整以下内容以适应您的机器人和模块的特定常量（所有距离单位必须以米为单位，旋转单位以弧度为单位）：</br>
这些说明主要可从以下步骤中进行跟踪：
1. 陀螺仪设置：```pigeonID```（确保陀螺仪旋转为逆时针+（逆时针正向）
2. ```chosenModule```： 
<br><b><ins>注意：并非每个模块的配置都经过测试。请参阅顶部的说明</b></ins>
<br>如果您使用受支持的COTS模块，请在此处设置您使用的模块和驱动比率。
<br>这将自动为特定模块设置功能所需的某些常量。
<br><b><ins>如果您没有使用受支持的COTS模块，则应删除此变量，并修复所有错误，并为您使用的模块设置正确的值</b></ins>
<br>如果您使用受支持的模块，这里是将自动设置的常量列表：
    * 车轮周长
    * 角度电机反转
    * 驱动电机反转
    * CANCoder传感器反转
    * 角度电机齿轮比
    * 驱动电机齿轮比
    * 角度Falcon电机PID值
    
3. ```trackWidth```：左右模块的中心到中心距离（米）。
4. ```wheelBase```：前后模块车轮的中心到中心距离（米）。
5. ```wheelCircumference```：车轮（包括胎面）的周长（米）。 <br><b>如果您使用受支持的模块，该值将自动设置。</b>
6. ```driveGearRatio```：驱动电机的总齿轮比。 <br><b>如果您使用受支持的模块，该值将自动设置。</b>
7. ```angleGearRatio```：角度电机的总齿轮比。 <br><b>如果您使用受支持的模块，该值将自动设置。</b>
8. ```canCoderInvert```和```angleMotorInvert```：两者都必须设置为逆时针+。 <br><b>如果您使用受支持的模块，该值将自动设置。</b>
9. ```driveMotorInvert```： <b>如果您使用受支持的模块，该值将自动设置。</b>
<br>这始终可以保持为false，因为您在第11步中设置偏移量，使得对驱动电机的正输入将导致机器人向前行驶。
<br>但是，如果由于某种原因您在设置偏移量时希望车轮上的锥齿轮朝一个方向或另一个方向，则可以将其设置为true。有关更多信息，请参阅第11步。

10. ```模块特定常量```：为各个模块设置电机和CANCoders的Can Id，参见下一步设置偏移量。
11. 设置偏移量
    * 为了找到偏移量，请使用一块1x1的金属，将其笔直地放在前后模块（左右两侧）的叉子上，以确保模块是直的。
    * 将所有车轮的锥齿轮指向同一方向（朝左或朝右），其中对驱动电机的正输入会使机器人向前移动（您可以使用Phoenix Tuner进行测试）。如果由于某种原因，您以车轮向后设置偏移量，则可以更改```driveMotorInvert```值以进行修正。
    * 打开smartdashboard（或shuffleboard并转到smartdashboard选项卡），您将看到4个名为“Mod 0 Cancoder”、“Mod 1 Cancoder”等的输出。
    <br>如果您已经将模块校直，请将这4个数字准确复制（精确到小数点后两位）到其各自的```angleOffset```常量中。
    <br><b>注意：</b>打印到smartdashboard的CANcoder值以度为单位，复制值到```angleOffset```时，您必须使用```Rotation2d.fromDegrees("copied value")```。

12. 角度电机PID值：<br><b>如果您使用受支持的模块，该值将自动设置。如果不是，或者您更喜欢更积极或更消极的响应，可以使用以下说明进行调整。</b> 
    * 要开始调整，请从较低的P值（0.01）开始。
    * 乘以10，直到模块开始围绕设定点振荡
    * 通过搜索值进行缩小（例如，如果它在P值为10时开始振荡，则尝试（10 -> 5 -> 7.5 -> 等）），直到模块不围绕设定点振荡。
    * 如果存在任何超调，可以通过重复相同的过程添加一些D，如果没有，请保持为0。I始终保持为0。

13. ```maxSpeed```：米每秒。 ```maxAngularVelocity```：弧度每秒。对于这些，您可以使用理论值，但最好物理驱动机器人并找到实际最大值。

14. 通过使用WPILib表征工具获得驱动表征值（KS，KV，KA），工具可以在[这里](https://docs.wpilib.org/en/stable/docs/software/wpilib-tools/robot-characterization/introduction.html)找到。您需要将模块锁定在正前方，并完成表征，就像是标准的坦克驱动。
15. ```driveKP```： 
<br>在完成表征并将KS，KV和KA值插入代码后，调整驱动电机kP，直到它没有超调且不在目标速度附近振荡。
<br>将```driveKI```和```driveKD```保留为0.0。


**控制器映射**
----
此代码本地设置为使用Xbox控制器控制万向轮驱动。 </br>
* 左摇杆：平移控制（前进和侧向移动）
* 右摇杆：旋转控制 </br>
* Y按钮：零陀螺仪（如果陀螺仪在比赛中途漂移，可以将机器人向前旋转并按Y键重新校正）
* 左肩键：切换为机器人中心控制（按住时）

**已测试模块**
----
| 供应商 | 模块 | 电机 | 配置（电机反转等） | 旋转电机调整 |
| :-------------: | :-------------: | :-------------: | :-------------: | :-------------: |
| WCP | SwerveX 标准  | Kraken X60 | <code style="color : red">未经测试</code> | <code style="color : red">未经测试</code> |
| WCP | SwerveX 标准  | Falcon 500 | <code style="color : red">未经测试</code> | <code style="color : red">未经测试</code> |
| WCP | SwerveX 翻转 | Kraken X60 | <code style="color : red">未经测试</code> | <code style="color : red">未经测试</code> |
| WCP | SwerveX 翻转 | Falcon 500 | <code style="color : red">未经测试</code> | <code style="color : red">未经测试</code> |
| SDS | MK3 | Kraken X60 | <code style="color : red">未经测试</code> | <code style="color : red">未经测试</code> |
| SDS | MK3 | Falcon 500 | <code style="color : green">已测试</code> | <code style="color : red">未经测试</code> |
| SDS | MK4 | Kraken X60 | <code style="color : red">未经测试</code> | <code style="color : red">未经测试</code> |
| SDS | MK4 | Falcon 500 | <code style="color : green">已测试</code> | <code style="color : red">未经测试</code> |
| SDS | MK4i | Kraken X60 | <code style="color : red">未经测试</code> | <code style="color : red">未经测试</code> |
| SDS | MK4i | Falcon 500 | <code style="color : green">已测试</code> | <code style="color : green">已测试</code> |


**变更日志**
---
* 1/4/24: 
    * 将Phoenix更新为Phoenix 6 - 24.0.0-beta-7
        * 实现了Phoenix 6的`SensorToMechanismRatio`，无需在代码中转换齿轮比
        * 实现了Phoenix 6的`ContinuousWrap`，无需自定义`SwerveModuleState.optimize`函数。
        * 更新了从Phoenix 5的单位（“计数”）到Phoenix 6的单位（旋转）的转换。
    * 将WPILib更新到2024.1.1-beta-4
    * 添加了SwerveX模块和Kraken X60电机
    * 从`Swerve`中直接使用陀螺仪角度作为航向（`getGyroYaw()`）改为使用测距输出的旋转`getHeading()`，符合WPILib的预期用途
    * 将存储库从364的Github中移除
