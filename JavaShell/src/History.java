import java.util.List;
import java.util.concurrent.BlockingQueue;


public class History extends Filter{
	public List<String> historyList;
	public History(BlockingQueue<Object> in, BlockingQueue<Object> out, List<String> historyList){super(in, out);
        this.historyList = historyList;
        this.in = in;
        this.out = out;
    }
	//Put history list into the in queue
	public void run(){
        for (int i = 0; i < historyList.size() - 1; i++)
			try {
				in.put(historyList.get(i));
			} catch (InterruptedException e) {}
	}
	
	//A sad use of transform
    public Object transform(Object o){
    	return o;
    }
}
