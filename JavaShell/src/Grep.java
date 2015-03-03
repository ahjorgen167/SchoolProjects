import java.util.List;
import java.lang.String;
import java.util.concurrent.BlockingQueue;

public class Grep extends Filter{
    public List<String> searchQueries;
    public Grep(
        BlockingQueue<Object> in, BlockingQueue<Object> out, List<String> searchQueries){super(in, out);
        this.searchQueries = searchQueries;
        this.in = in;
        this.out = out;
    }
    
    @Override
    public Object transform(Object o){
    	try{
    	int j = 0;
    	for(int i = 0; i < searchQueries.size(); i++){
    		if(o.toString().contains(searchQueries.get(i)))
    			j++;
    	}
    	if(j!=searchQueries.size())
            o = null;
    	}catch(Exception e){}
    	return o;
    }

}
