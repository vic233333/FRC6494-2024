**<ins>注意:</ins> 尽管该代码旨在为商用现成模块（COTS Modules）提供即插即用的配置/调校数值，但在升级到 Phoenix 6 后，一些配置/调校尚未经过测试。如果此代码用于这些未经测试的模块，模块特定的配置和调校可能需要进行编辑。**

**如果有人愿意贡献一个经过测试的配置/调校，请发送消息或提交 PR。**

**向下滚动查看已测试/未测试配置的完整列表。**

# BaseTalonFXSwerve </br>

**TalonFX 控制器、CTRE CANCoder 和 CTRE Pigeon 陀螺仪的基本全向轮代码** </br>
该代码设计适用于 Swerve Drive Specialties 的 MK3、MK4、MK4i 和 WCP SwerveX 模块，但也可以轻松适配其他样式的模块。</br>

**设置常量**
----
以下内容必须在 Constants.java 文件中根据您的机器人和模块的具体常量进行调整（所有距离单位必须为米，旋转单位为弧度）：</br>

1. 陀螺仪设置: ```pigeonID```（确保陀螺仪旋转是逆时针为正）
2. ```chosenModule```: 
<br><b><ins>注意: 不是每个模块的配置都经过测试。参见顶部的说明。</b></ins>
<br>如果您使用的是受支持的 COTS 模块，请在此处设置您正在使用的模块和驱动比率。
<br>这将自动为该特定模块设置某些常量，以便模块正常工作。
<br><b><ins>如果您没有使用受支持的 COTS 模块，您应该删除此变量，并为您使用的模块用正确的值修复所有错误。</b></ins>
<br> 如果您使用的是受支持的模块，这里是自动设置的常量列表：
    * 轮子周长
    * 角度电机反转
    * 驱动电机反转
    * CANCoder 传感器反转
    * 角度电机齿轮比
    * 驱动电机齿轮比
    * 角度电机 PID 值
    
3. ```trackWidth```: 左右模块中心到中心的距离，单位为米。
4. ```wheelBase```: 前后模块轮子中心到中心的距离，单位为米。
5. ```wheelCircumference```: 轮子（包括胎面）的周长，单位为米。<br><b>如果您使用的是受支持的模块，此值将自动设置。</b>
6. ```driveGearRatio```: 驱动电机的总齿轮比。<br><b>如果您使用的是受支持的模块，此值将自动设置。</b>
7. ```angleGearRatio```: 角度电机的总齿轮比。<br><b>如果您使用的是受支持的模块，此值将自动设置。</b>
8. ```canCoderInvert``` 和 ```angleMotorInvert```: 都必须设置为逆时针为正。<br><b>如果您使用的是受支持的模块，此值将自动设置。</b>
9. ```driveMotorInvert```: <b>如果您使用的是受支持的模块，此值将自动设置。</b>
<br>由于您在第 11 步中设置了偏移，因此始终可以保持为 false，以便驱动电机的正输入将使机器人向前移动。
<br>不过，如果由于某些原因您更喜欢在设置偏移时将轮子上的斜齿轮面向一个方向或另一个方向，则可以将其设置为 true。有关更多信息，请参阅第 11 步。

10. ```模块特定常量```: 为各个模块设置电机和 CANCoder 的 CAN ID，查看下一步以设置偏移。
11. 设置偏移
    * 要查找偏移量，请使用一块 1x1 的金属，使其紧贴前后模块的支架（在左右两侧），以确保模块是直的。
    * 将所有轮子的斜齿轮指向同一方向（要么面向左，要么面向右），其中驱动电机的正输入驱动机器人向前移动（可以使用 Phoenix Tuner 进行测试）。如果由于某些原因您在车轮向后时设置了偏移，则可以更改 ```driveMotorInvert``` 值来修复。
    * 打开 smartdashboard（或 shuffleboard 并进入 smartdashboard 选项卡），您将看到 4 个名为 "Mod 0 Cancoder"、"Mod 1 Cancoder" 等的输出。
    <br>如果您已将模块调直，请将这四个数字精确地复制（到小数点后两位）到其各自的 ```angleOffset``` 变量中。
    <br><b>注意:</b> 打印到 smartdashboard 的 CANcoder 值为角度值，当将这些值复制到 ```angleOffset``` 时，必须使用 ```Rotation2d.fromDegrees("复制的值")```。

12. 角度电机 PID 值: <br><b>如果您使用的是受支持的模块，此值将自动设置。如果不是，或者您更喜欢更积极或更不激进的响应，您可以使用以下说明进行调试。</b> 
    * 从较小的 P 值（0.01）开始进行调试。
    * 乘以 10，直到模块开始在设定点周围振荡。
    * 通过查找数值来缩小搜索范围（例如，如果它在 P 值为 10 时开始振荡，则尝试（10 -> 5 -> 7.5 -> 等）），直到模块不在设定点周围振荡。
    * 如果有任何过冲，您可以通过重复相同的过程添加一些 D，如果没有则保持为 0。始终保持 I 为 0。

13. ```maxSpeed```: 单位为米/秒。 ```maxAngularVelocity```: 单位为弧度/秒。对于这些值，您可以使用理论值，但更好的是物理驱动机器人并找到实际的最大值。

14. 通过使用 WPILib 表征工具获得驱动表征值 (KS, KV, KA)，请访问[此处](https://docs.wpilib.org/en/stable/docs/software/wpilib-tools/robot-characterization/introduction.html)。您将需要将模块锁定向前，并按照标准履带驱动方式完成表征。
15. ```driveKP```: 
<br>在完成表征并将 KS、KV 和 KA 值插入代码后，调整驱动电机 kP 直到其不超调并且不在目标速度周围振荡。
<br>将 ```driveKI``` 和 ```driveKD``` 保持为 0.0。

**控制器映射**
----
此代码本机支持使用 Xbox 控制器来控制全向轮驱动。</br>
* 左摇杆: 平移控制（向前和侧向移动）
* 右摇杆: 旋转控制</br>
* Y 按钮: 归零陀螺仪（如果陀螺仪在比赛中漂移，将机器人旋转到向前，然后按 Y 重新归零）
* 左侧按钮: 在按住时切换到机器人中心控制

**测试过的模块**
----
| 供应商 | 模块 | 电机 | 配置（电机反转等） | 旋转电机调试 |
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
    * 将 Phoenix 更新到 Phoenix 6 - 24.0.0-beta-7
        * 实现了 Phoenix 6 的 `SensorToMechanismRatio`，不再需要在代码中转换齿轮比。
        * 实现了 Phoenix 6 的 `ContinuousWrap`，不再需要自定义 `SwerveModuleState.optimize` 函数。
        * 更新了从 Phoenix 5 的单位（“计数”）到 Phoenix 6 的单位（旋转）。
    * 更新了 WPILib 到 2024.1.1-beta-4
    * 添加了 SwerveX 模块和 Kraken X60 电机
    * 从直接使用 `Swerve` 中的陀螺仪角度（`getGyroYaw()`）到使用 WPILib 意图的测距输出 `getHeading()`。
    * 从 364 的 Github 中移出了此代码库
