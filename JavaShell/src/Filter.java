import java.util.concurrent.*;

/*w
 * This is the Filter class all you command implementation needs to extend.
 */
public abstract class Filter extends Thread {
	protected BlockingQueue<Object> in;
	protected BlockingQueue<Object> out;
	protected volatile boolean done;
	/*
	 * The following flag is for Part 4.
	 */
	protected volatile boolean killed;

	public Filter (BlockingQueue<Object> in, BlockingQueue<Object> out) {
		this.in = in;
		this.out = out;
		this.done = false;
		this.killed = false;
	}

	/*
	 * This is for Part 4.
	 */
	public void cmdKill() {
		this.killed = true;
	}
	/*
	 * This method need to be overridden.
	 * @see java.lang.Thread#run()
	 */
	public void run() {
        Object o = null;
        while(in.isEmpty())
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        while(!in.isEmpty()) {
			// read from input queue, may block
            try {
				o = in.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}    

			// allow filter to change message
            o = transform(o); 

			// forward to output queue
            try {
				out.put(o);
			} catch (/*Interrupted*/Exception e) {
				//e.printStackTrace();
			}       
        }
	}

	/*
	 * This method might need to be overridden.
	 */
	public abstract Object transform(Object o);
}