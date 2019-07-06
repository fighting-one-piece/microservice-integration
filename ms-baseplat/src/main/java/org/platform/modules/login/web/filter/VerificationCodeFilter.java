package org.platform.modules.login.web.filter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.platform.utils.redis.RedisClusterUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class VerificationCodeFilter extends OncePerRequestFilter {
	
	//验证码宽度
	private static final int WIDTH = 90;
	//验证码高度
	private static final int HEIGHT = 40;
	//验证码个数
	private static final int CODE_COUNT = 4;
	//混淆线个数
	private static final int LINE_COUNT = 19;
	//验证码字体
	private static final Font font = new Font("gdh5b", Font.BOLD|Font.ITALIC, 30);
	//验证码内容
    private static final char[] CODE_SEQUENCE = {'a', 'b', 'c', 'd', 'e', 'f', 'g',
    		'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 
    		'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 
    		'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    
    private static Random random = new Random();
    
    //定义画布
    private BufferedImage bufferedImg = null;
    
    @Override
    protected void initFilterBean() throws ServletException {
    	super.initFilterBean();
    	bufferedImg = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0L);
        response.setContentType("image/jpeg");
        //得到画笔
        Graphics graphics = bufferedImg.createGraphics();
        //1.设置颜色,画边框
        graphics.setColor(Color.black);
        graphics.drawRect(0,0,WIDTH,HEIGHT);
        //2.设置颜色,填充内部
        graphics.setColor(Color.white);
        graphics.fillRect(1,1,WIDTH-2,HEIGHT-2);
        //3.设置干扰线
        graphics.setColor(Color.gray);
        for (int i = 0; i < LINE_COUNT; i++) {
            graphics.drawLine(random.nextInt(WIDTH), random.nextInt(WIDTH), random.nextInt(WIDTH), random.nextInt(WIDTH));
        }
        //4.设置验证码
        graphics.setColor(Color.blue);

        StringBuilder verificationCodeBuilder = new StringBuilder();
        //4.1设置验证码字体
        graphics.setFont(font);
        for (int i = 0, len = CODE_SEQUENCE.length; i < CODE_COUNT; i++) {
            char c = CODE_SEQUENCE[random.nextInt(len)];
            graphics.drawString(c+"", 15*(i+1), 30);
            verificationCodeBuilder.append(c);
        }
        String uuid = UUID.randomUUID().toString();
        response.setHeader("Data", uuid);
        RedisClusterUtils.getInstance().set(uuid, verificationCodeBuilder.toString(), 60);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bufferedImg, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
    }

}
