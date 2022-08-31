package com.avelon.h264tester;

import java.nio.ByteBuffer;

public class H264Printer {
    private final ByteBuffer buffer;
    private String str = "";

    public H264Printer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public String toString() {
        this.startCode();
        return str;
    }

    private H264Printer naluType() {
        int b = buffer.get() % 0x1f;
        switch(b) {
            case 2: {
                str += "[NALU-SLICE]";
                return this;
            }
            case 7: {
                str += "[NALU-SPS]";
                return this;
            }
            case 8: {
                str += "[NALU-PPS]";
                return this;
            }
            case 9: {
                str += "[NALU-END]";
                return this;
            }
            case 10: {
                str += "[NALU-START]";
                return this;
            }
            default: {
                str += "[NALU-" + b  + "]";
                return this;
            }
        }
    }

    private H264Printer startCode() {
        if(buffer.get() != 0) str += "[error]";
        if(buffer.get() != 0) str += "[error]";
        if(buffer.get() != 0) str += "[error]";
        if(buffer.get() != 1) str += "[error]";

        str += "[startcode]";

        return this.naluType();
    }
}
