import stomp
conn = stomp.Connection([('localhost', 61613)])
conn.connect(username='guest', password='guest', wait=True)
print("连接成功！")
conn.disconnect()