package com.reign.gcld.player.action;

import com.reign.gcld.common.web.*;
import com.reign.framework.netty.mvc.view.*;
import com.reign.gcld.system.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import com.reign.gcld.player.common.*;
import com.reign.gcld.common.util.*;
import java.awt.font.*;
import java.text.*;
import com.reign.gcld.player.dto.*;
import javax.imageio.*;
import java.io.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.framework.netty.mvc.result.*;
import java.util.*;
import com.reign.util.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.plug.*;
import com.reign.gcld.common.*;
import com.jhlabs.image.*;
import java.awt.*;
import java.awt.image.*;

@Action
@Views({ @View(name = "image", type = ImageView.class), @View(name = "byte", type = ByteView.class) })
public class ValidateCodeAction extends BaseAction
{
    private static final long serialVersionUID = -576339067745209494L;
    private static final Logger log;
    public static Map<Integer, CodeCount> codeCountMap;
    private static final int width = 100;
    private static final int height = 50;
    private static final int codeY = 35;
    private static final float yawpRate = 0.015f;
    private static final long CODE_TIME_OUT = 300000L;
    private static final long CODE_INPUT_ERROR_FIVE = 1800000L;
    public static Map<Integer, Code> playerCodeMap;
    @Autowired
    private ISystemService systemService;
    
    static {
        log = CommonLog.getLog(ValidateCodeAction.class);
        ValidateCodeAction.codeCountMap = new ConcurrentHashMap<Integer, CodeCount>();
        ValidateCodeAction.playerCodeMap = new ConcurrentHashMap<Integer, Code>();
    }
    
    @Command("code@getInputStream")
    public ImageResult getInputStream(final Request request, @RequestParam("userkey") final String userkey) {
        final Font[] fonts = CodeConfig.getFonts();
        final BufferedImage buffImg = new BufferedImage(100, 50, 1);
        final Graphics2D g = buffImg.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 100, 50);
        Font font;
        if (WebUtil.isFt()) {
            font = fonts[0];
        }
        else {
            font = fonts[WebUtil.nextInt(fonts.length)];
        }
        font = font.deriveFont(0, 23.0f);
        g.setFont(font);
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(0, 0, 99, 49);
        for (int i = 0; i < 2; ++i) {
            final int x = WebUtil.nextInt(5);
            final int y = WebUtil.nextInt(50);
            final int xl = 140 + WebUtil.nextInt(11);
            final int yl = WebUtil.nextInt(50);
            this.drawThickLine(g, x, y, x + xl, y + yl, 3, new Color(WebUtil.nextInt(255), WebUtil.nextInt(255), WebUtil.nextInt(255)));
        }
        for (int area = 75, j = 0; j < area; ++j) {
            final int x2 = WebUtil.nextInt(100);
            final int y2 = WebUtil.nextInt(50);
            final int rgb = this.getRandomIntColor();
            buffImg.setRGB(x2, y2, rgb);
        }
        final StringBuffer randomCode = new StringBuffer();
        int red = 0;
        int green = 0;
        int blue = 0;
        final String strRand = String.valueOf(CodeConfig.getRandomWords());
        for (int size = strRand.length(), k = 0; k < size; ++k) {
            final String temp = strRand.substring(k, k + 1);
            red = WebUtil.nextInt(155);
            green = WebUtil.nextInt(155);
            blue = WebUtil.nextInt(155);
            final AttributedString as = new AttributedString(temp);
            as.addAttribute(TextAttribute.SIZE, WebUtil.nextInt(4) + 5);
            as.addAttribute(TextAttribute.KERNING, 10);
            as.addAttribute(TextAttribute.FOREGROUND, new Color(red, green, blue));
            as.addAttribute(TextAttribute.FONT, font);
            g.drawString(as.getIterator(), 3 * (12 / size) + k * (100 / size - 5), 35);
            randomCode.append(temp);
        }
        int playerId = 0;
        PlayerDto playerDto = null;
        final Session session = SessionManager.getInstance().getSession(userkey);
        if (session != null) {
            playerDto = (PlayerDto)session.getAttribute("PLAYER");
        }
        if (playerDto != null) {
            playerId = playerDto.playerId;
        }
        else {
            ValidateCodeAction.log.error("ValidateCodeAction playerId = 0");
        }
        ValidateCodeAction.playerCodeMap.put(playerId, new Code(randomCode.toString(), System.currentTimeMillis()));
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(buffImg, "jpeg", bos);
            return new ImageResult(bos.toByteArray());
        }
        catch (IOException e) {
            ValidateCodeAction.log.error("", e);
            return null;
        }
    }
    
    @Command("code@timeout")
    public ByteResult timeout(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        final Date nowDate = new Date();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final CodeCount codeCount = ValidateCodeAction.codeCountMap.get(playerDto.playerId);
        if (codeCount != null && codeCount.count >= WebUtil.getCodeWrongLimit() && CDUtil.isInCD(1800000L, codeCount.timestamp, nowDate)) {
            doc.createElement("code", false);
            doc.createElement("time", CDUtil.getCD(1800000L, codeCount.timestamp, nowDate));
            doc.endObject();
            return this.getResult(JsonBuilder.getJson(State.SUCCESS, doc.toByte()), request);
        }
        doc.createElement("code", true);
        doc.endObject();
        return this.getResult(JsonBuilder.getJson(State.SUCCESS, doc.toByte()), request);
    }
    
    @Command("code@codeCheck")
    public ByteResult codeCheck(@RequestParam("code") final String code, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        final Date nowDate = new Date();
        final CodeCount codeCount = ValidateCodeAction.codeCountMap.get(playerDto.playerId);
        if (codeCount != null && codeCount.count >= WebUtil.getCodeWrongLimit()) {
            if (CDUtil.isInCD(1800000L, codeCount.timestamp, nowDate)) {
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("code", false);
                doc.createElement("time", CDUtil.getCD(1800000L, codeCount.timestamp, nowDate));
                doc.endObject();
                return this.getResult(JsonBuilder.getJson(State.SUCCESS, doc.toByte()), request);
            }
            codeCount.count = 0;
        }
        Code sessionCode = ValidateCodeAction.playerCodeMap.get(playerDto.playerId);
        if (sessionCode == null) {
            sessionCode = ValidateCodeAction.playerCodeMap.get(0);
            if (sessionCode == null) {
                ValidateCodeAction.log.error("ValidateCodeAction codeCheck return playerId" + playerDto.playerId);
                return this.getResult(JsonBuilder.getJson(State.FAIL, "fail"), request);
            }
            ValidateCodeAction.log.error("ValidateCodeAction codeCheck get(0) playerId" + playerDto.playerId);
        }
        if (code != null && code.equalsIgnoreCase(sessionCode.code)) {
            final long cd = CDUtil.getCD(300000L, sessionCode.timestamp, new Date());
            final int value = TimeSlice.getInstance().getUnBlockCount(playerDto.playerId);
            if (-1 != value && cd > 0L && !ValidateCodeAction.codeCountMap.containsKey(playerDto.playerId) && TimeSlice.getInstance().needBlock(playerDto.playerId)) {
                this.systemService.blockReward(playerDto.playerId, value);
            }
            if (TimeSlice.getInstance().needBlock(playerDto.playerId)) {
                ValidateCodeAction.codeCountMap.remove(playerDto.playerId);
            }
            TimeSlice.getInstance().unBlock(playerDto, System.currentTimeMillis() + 1800000L);
            final JsonDocument doc2 = new JsonDocument();
            doc2.startObject();
            doc2.createElement("res", true);
            doc2.endObject();
            return this.getResult(JsonBuilder.getJson(State.SUCCESS, doc2.toByte()), request);
        }
        ValidateCodeAction.log.error("ValidateCodeAction codeCheck code:" + code + " sessionCode:" + sessionCode.code);
        CodeCount value2 = ValidateCodeAction.codeCountMap.get(playerDto.playerId);
        if (value2 == null) {
            value2 = new CodeCount(1, nowDate.getTime());
        }
        else {
            final CodeCount codeCount2 = value2;
            ++codeCount2.count;
            value2.timestamp = nowDate.getTime();
        }
        if (value2.count >= WebUtil.getCodeWrongLimit()) {
            final JsonDocument doc3 = new JsonDocument();
            doc3.startObject();
            doc3.createElement("code", false);
            doc3.createElement("time", CDUtil.getCD(1800000L, codeCount.timestamp, nowDate));
            doc3.endObject();
            return this.getResult(JsonBuilder.getJson(State.SUCCESS, doc3.toByte()), request);
        }
        ValidateCodeAction.codeCountMap.put(playerDto.playerId, value2);
        return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_CODE_31002), request);
    }
    
    private void shear(final Graphics2D g, final int w1, final int h1, final Color color) {
        this.shearX(g, w1, h1, color);
        this.shearY(g, w1, h1, color);
    }
    
    private void shearX(final Graphics2D g, final int w1, final int h1, final Color color) {
        final int period = WebUtil.nextInt(2);
        final boolean borderGap = true;
        final int frames = 2;
        final int phase = WebUtil.nextInt(2);
        for (int i = 0; i < h1; ++i) {
            final double d = (period >> 1) * Math.sin(i / period + 6.283185307179586 * phase / frames);
            g.copyArea(0, i, w1, 1, (int)d, 0);
            if (borderGap) {
                g.setColor(color);
                g.drawLine((int)d, i, 0, i);
                g.drawLine((int)d + w1, i, w1, i);
            }
        }
    }
    
    private void shearY(final Graphics2D g, final int w1, final int h1, final Color color) {
        final int period = WebUtil.nextInt(20) + 10;
        final boolean borderGap = true;
        final int frames = 20;
        final int phase = 7;
        for (int i = 0; i < w1; ++i) {
            final double d = (period >> 1) * Math.sin(i / period + 6.283185307179586 * phase / frames);
            g.copyArea(i, 0, 1, h1, 0, (int)d);
            if (borderGap) {
                g.setColor(color);
                g.drawLine(i, (int)d, i, 0);
                g.drawLine(i, (int)d + h1, i, h1);
            }
        }
    }
    
    private int getRandomIntColor() {
        final int[] rgb = { WebUtil.nextInt(255), WebUtil.nextInt(255), WebUtil.nextInt(255) };
        int color = 0;
        int[] array;
        for (int length = (array = rgb).length, i = 0; i < length; ++i) {
            final int c = array[i];
            color <<= 8;
            color |= c;
        }
        return color;
    }
    
    private void drawThickLine(final Graphics2D g, final int x1, final int y1, final int x2, final int y2, final int thickness, final Color c) {
        g.setColor(c);
        final int dx = x2 - x1;
        final int dy = y2 - y1;
        final double lineLength = Math.sqrt(dx * dx + dy * dy);
        final double scale = thickness / (2.0 * lineLength);
        double ddx = -scale * dy;
        double ddy = scale * dx;
        ddx += ((ddx > 0.0) ? 0.5 : -0.5);
        ddy += ((ddy > 0.0) ? 0.5 : -0.5);
        final int dX = (int)ddx;
        final int dY = (int)ddy;
        final int[] xPoints = new int[4];
        final int[] yPoints = new int[4];
        xPoints[0] = x1 + dX;
        yPoints[0] = y1 + dY;
        xPoints[1] = x1 - dX;
        yPoints[1] = y1 - dY;
        xPoints[2] = x2 - dX;
        yPoints[2] = y2 - dY;
        xPoints[3] = x2 + dX;
        yPoints[3] = y2 + dY;
        g.fillPolygon(xPoints, yPoints, 4);
    }
    
    private BufferedImage getDistortedImage(final BufferedImage baseImage) {
        final BufferedImage distortedImage = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), 1);
        final Graphics2D graphics = (Graphics2D)distortedImage.getGraphics();
        final RippleFilter rippleFilter = new RippleFilter();
        rippleFilter.setWaveType(0);
        rippleFilter.setXAmplitude(2.6f);
        rippleFilter.setYAmplitude(1.7f);
        rippleFilter.setXWavelength(15.0f);
        rippleFilter.setYWavelength(5.0f);
        rippleFilter.setEdgeAction(0);
        final WaterFilter waterFilter = new WaterFilter();
        waterFilter.setAmplitude(1.5f);
        waterFilter.setPhase(10.0f);
        waterFilter.setWavelength(1.0f);
        BufferedImage effectImage = waterFilter.filter(baseImage, (BufferedImage)null);
        effectImage = rippleFilter.filter(effectImage, (BufferedImage)null);
        graphics.drawImage(effectImage, 0, 0, null, null);
        graphics.dispose();
        return distortedImage;
    }
    
    private class Code
    {
        public String code;
        public long timestamp;
        
        public Code(final String code, final long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }
    }
    
    private class CodeCount
    {
        public int count;
        public long timestamp;
        
        public CodeCount(final int count, final long timestamp) {
            this.count = count;
            this.timestamp = timestamp;
        }
    }
}
