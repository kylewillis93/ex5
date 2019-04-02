from bluetooth import *
import keyboard
server_sock=BluetoothSocket( RFCOMM )
server_sock.bind(("",PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid = "d374bfaf-e97b-4bf2-af5d-f58038109686"

advertise_service( server_sock, "VocalChords",
                   service_id = uuid,
                   service_classes = [ uuid, SERIAL_PORT_CLASS ],
                   profiles = [ SERIAL_PORT_PROFILE ], 
#                   protocols = [ OBEX_UUID ] 
                    )
                   
print("Waiting for connection on RFCOMM channel %d" % port)

client_sock, client_info = server_sock.accept()
print("Accepted connection from ", client_info)

try:
    while True:
        data = client_sock.recv(1024)
        if len(data) == 0: break
        print("received [%s]" % data)
        if data == "Up":
            keyboard.press_and_release("Up")
        elif data == "Down":
            keyboard.press_and_release("Down")
        elif data == "Left":
            keyboard.press_and_release("Left")
        elif data == "Right":
            keyboard.press_and_release("Right")
        
except IOError:
    pass

print("disconnected")

client_sock.close()
server_sock.close()
print("all done")
