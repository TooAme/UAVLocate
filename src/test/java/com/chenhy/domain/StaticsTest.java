package com.chenhy.domain;

import static com.chenhy.domain.StaticsTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.chenhy.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StaticsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Statics.class);
        Statics statics1 = getStaticsSample1();
        Statics statics2 = new Statics();
        assertThat(statics1).isNotEqualTo(statics2);

        statics2.setId(statics1.getId());
        assertThat(statics1).isEqualTo(statics2);

        statics2 = getStaticsSample2();
        assertThat(statics1).isNotEqualTo(statics2);
    }
}
