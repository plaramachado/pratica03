package video.unicast;


public enum RTSPMessageType {
	SETUP{
		public String toString(){
			return "SETUP";
		}
	}, PLAY{
		public String toString(){
			return "PLAY";
		}
	}, PAUSE{
		public String toString(){
			return "PAUSE";
		}
	}, TEARDOWN{
		public String toString(){
			return "TEARDOWN";
		}
	}
}
