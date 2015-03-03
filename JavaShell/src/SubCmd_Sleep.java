import java.util.concurrent.BlockingQueue;

/*
 * This is only partially implemented and the duration is set to 5 seconds by default. 
 * Your implementation should be able to set duration by passing argument.
 */
public class SubCmd_Sleep extends Filter {
	long duration = 5;

	public SubCmd_Sleep(BlockingQueue<Object> in, BlockingQueue<Object> out, long duration) {
		super(in, out);
        this.duration = duration;
        this.in = in;
        this.out = out;
	}

	@Override
	public void run() {
//		if(duration < 0){
//			System.out.println("Sleep error: invalid argument.");
//			return;
//		}
		while (!this.done) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println(Thread.currentThread().getName() + " 's sleep has been interrupted.");
			}
//					System.out.println("Sleep: " + --duration + " seconds left.");
					try {
						in.put("Sleep: " + --duration + " seconds left.");
					} catch (InterruptedException e) {}
//				out.put("Sleep: " + --duration + " seconds left.");
			if (duration <= 0) {
				this.done = true;
			}
		}
	}

	@Override
	public Object transform(Object o) {
		return null;
	}
}
