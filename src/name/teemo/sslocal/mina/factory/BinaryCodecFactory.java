package name.teemo.sslocal.mina.factory;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class BinaryCodecFactory implements ProtocolCodecFactory{

	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		ProtocolDecoder pd = new ProtocolDecoder(){
			@Override
			public void decode(IoSession session, IoBuffer ioBuffer,ProtocolDecoderOutput out) throws Exception {
		          if (!(ioBuffer instanceof IoBuffer)){   
		        	  out.write("".getBytes());
		          }else{
		        	  ioBuffer.rewind();
			          byte[] bytes = new byte [ioBuffer.limit()];   
			          ioBuffer.get(bytes);    
			          out.write(bytes);
		          }
			}
			@Override
			public void dispose(IoSession session) throws Exception {
			}
			@Override
			public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
			}
		};
		return pd;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
        ProtocolEncoder pe = new ProtocolEncoder() {  
            @Override  
            public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {  
            	IoBuffer ioBuffer = IoBuffer.allocate(8);
            	ioBuffer.setAutoExpand(true);
            	ioBuffer.put((byte[])message);
            	ioBuffer.flip(); 
            	out.write(ioBuffer);
            }  
            @Override  
            public void dispose(IoSession session) throws Exception {  
            }		
        };  
        return pe;  
	}

}
