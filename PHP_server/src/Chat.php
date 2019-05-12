<?php
namespace MyApp;
use Ratchet\MessageComponentInterface;
use Ratchet\ConnectionInterface;

class Chat implements MessageComponentInterface {
    //protected $clients;
	
	protected $client1;
	protected $client2;
	
	protected $connectedClients;
	
	protected $send2All;
	
    public function __construct() {
        //$this->clients = new \SplObjectStorage;
		$this->connectedClients = 0;
		$this->send2All = false;
		echo "Server started\n";
    }
    public function onOpen(ConnectionInterface $conn) {
        // Store the new connection to send messages to later
        //$this->clients->attach($conn);
		
		if ($this->connectedClients == 0) {
			$this->client1 = $conn;
			$this->connectedClients = 1;
			echo "client 1 connected! ({$conn->resourceId})\n";
		}
		// 1 client does 2 onOpen ??? (dunno why)
		if ($this->connectedClients == 1 && $this->client1->resourceId !== $conn->resourceId) {
			$this->client2 = $conn;
			$this->connectedClients = 2;
			echo "client 2 connected! ({$conn->resourceId})\n";
		}
    }
    
	public function onMessage(ConnectionInterface $from, $msg) {
        /*$numRecv = count($this->clients) - 1;
        echo sprintf('Connection %d sending message "%s" to %d other connection%s' . "\n"
            , $from->resourceId, $msg, $numRecv, $numRecv == 1 ? '' : 's');*/
        if($this->send2All == false){
			if ($from == $this->client1) {
			if ($this->connectedClients > 1) {
				$this->client2->send($msg);
			}
			}
			if ($from == $this->client2) {
			if ($this->connectedClients > 0) {
				$this->client1->send($msg);
			}
			}
		}
		else {
			if ($this->connectedClients > 0) {
				$this->client1->send($msg);
			}
			if ($this->connectedClients > 1) {
				$this->client2->send($msg);
			}
		}
		
		/*foreach ($this->clients as $client) {
            if ($from !== $client) {
                // The sender is not the receiver, send to each client connected
                $client->send($msg);
            }
        }*/
    }
	
    public function onClose(ConnectionInterface $conn) {
        // The connection is closed, remove it, as we can no longer send it messages
        //$this->clients->detach($conn);
        if ($conn == $this->client1) {
			$this->client1 = $this->client2;
			$this->connectedClients = 0;
			echo "client 1 disconnected! ({$conn->resourceId})\n";			
		}
        if ($conn == $this->client2) {
			$this->connectedClients = 1;
			echo "client 2 disconnected! ({$conn->resourceId})\n";			
		}
		$this->client1->send("disconnected");
    }
	
    public function onError(ConnectionInterface $conn, \Exception $e) {
        echo "An error has occurred: {$e->getMessage()}\n";
        
		$conn->close();
    }
}
?>