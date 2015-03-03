import java.util.List;
import java.util.concurrent.BlockingQueue;

//This class is used if there is no grep statement
public class SwitchStack extends Filter {
	public SwitchStack(BlockingQueue<Object> in, BlockingQueue<Object> out) {
		super(in, out);
		this.in = in;
		this.out = out;
	}

	//Move information without changing it.  But hey!  I'm using inheritance
	@Override
	public Object transform(Object o) {
		return o;
	}
}
