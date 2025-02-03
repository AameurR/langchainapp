package com.guestu.langchaindemoapp.ai.segmenttransformers;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.data.segment.TextSegmentTransformer;

import java.util.List;

public class ResumeSegementTransformer implements TextSegmentTransformer {



    @Override
    public TextSegment transform(TextSegment textSegment) {
        return textSegment; //Todo implement transformations

      // add prefixes to filter out the segments
        // create metadatas

    }

    @Override
    public List<TextSegment> transformAll(List<TextSegment> segments) {
        return segments.stream().map(this::transform).toList();
    }
}
