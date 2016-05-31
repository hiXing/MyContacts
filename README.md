# 通讯录实现
	参考链接：http://blog.csdn.net/wwj_748/article/details/19965913

##	登录注册：

	用户首次使用app必须使用手机号注册账户，注册成功后自动记录注册信息，并返回登录界面；
	用户输入用户名密码，并通过验证，才能进入主界面;
	（目前账户信息通过SharedPreferences保存在本地，以后可以通过服务端验证）

##	个人中心
	
	显示 用户的头像、昵称、年龄、电话、邮箱、生日，点击右上角编辑按钮可以修改个人信息

##	通话记录
		
	通过查询读取通讯录数据库contacts2.db获取通话记录；
	可点击记录拨打电话，
	
	
##	联系人

	联系人列表，可以对联系人进行编辑、删除、添加；


##	短信
	
	获取短信列表，在android4.4以后Google 更改了短信的权限机制，直接获取短信记录会有问题；
	参考文章：http://blog.csdn.net/krislight/article/details/12868603
			  http://blog.csdn.net/cb269267/article/details/20038277?utm_source=tuicool&utm_medium=referral

----	

Tips：别忘了在AndroidManifest.xml中添加各种请求权限