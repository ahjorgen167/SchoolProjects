import java.util.concurrent.BlockingQueue;

public class PrintBlockingQueue extends Filter {
    public PrintBlockingQueue(    
    BlockingQueue<Object> in,
    BlockingQueue<Object> out) {super(in, out);} 
    @Override
    public void run(){
    	
    	//Variables
        Object o = "";
        int i = 0;
        
        //Wait for output loop
        while(out.isEmpty() && i < 10){
        	try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
        	i++;
            if(!out.isEmpty()){
            	try {
					Thread.sleep(10);
				} catch (InterruptedException e) {}
            	break;
            }
        }
        
        //Print to screen loop
        while(true){
        try {
            o = this.out.take();
            o = transform(o);
        } catch (InterruptedException ex) {}
        if(out.isEmpty())
        	break;}

    }
    
    //A really stupid method
    public Object transform(Object o){
        System.out.println(o);
        return o;
    }    
    
}