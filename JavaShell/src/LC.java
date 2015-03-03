import java.util.concurrent.BlockingQueue;

public class LC extends Filter {
	public LC(BlockingQueue<Object> in, BlockingQueue<Object> out) {
		super(in, out);
		this.in = in;
		this.out = out;
	}

	@Override
	public void run() {
//		here: while (!in.isEmpty()) {
//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//			}
//			if (!out.isEmpty() && in.isEmpty())
//				break here;
//		}
//		try {
//			Thread.sleep(10);
//		} catch (InterruptedException e) {
//		}
//		if (in.isEmpty() && !out.isEmpty())
			try {
				int temp = out.size();
				if(out.isEmpty())
					temp = 0;
				out.clear();
				out.put(temp);
			} catch (InterruptedException e) {}
	}
	

	// public synchronized int getSize(){
	// while(!in.isEmpty())
	// try {
	// Thread.sleep(10);
	// wait();
	// } catch (InterruptedException e) {}
	// int temp = out.size();
	// notify();
	// return temp;
	// }
	public Object transform(Object o) {
		return o;
	}
}
