package video.unicast;


public enum RTSPState {
	INIT{
		public String toString(){
			return "INIT";
		}
	}, READY{
		public String toString(){
			return "READY";
		}
	}, PLAYING{
		public String toString(){
			return "PLAYING";
		}
	}, DONE{
		public String toString(){
			return "DONE";
		}
	}
	
}
