package org.fuad.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.util.Collections;
import org.fuad.exception.FailedScrappingException;
import org.fuad.model.Product;
import org.fuad.scrapper.TokopediaScrapper;
import org.fuad.scrapper.TokopediaScrapper.Category;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TokopediaScrapperService.class)
public class TokopediaScrapperServiceTest {

    @Mock
    private TokopediaScrapper scrapper;

    private TokopediaScrapperService service;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        service = new TokopediaScrapperService(scrapper);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenProblematicFileWhenDownloadCsvThenShouldThrow() throws Exception {
        // arrange
        final Category category = Category.HANDPHONE;
        final int count = 10;
        CsvMapper csvMapper = mock(CsvMapper.class);
        CsvSchema schema = mock(CsvSchema.class);
        when(scrapper.extractProductList(category, count)).thenReturn(Collections.emptyList());
        whenNew(CsvMapper.class).withNoArguments().thenReturn(csvMapper);
        when(csvMapper.schemaFor(Product.class)).thenReturn(schema);
        when(schema.withHeader()).thenReturn(schema);
        when(csvMapper.writer(any(CsvSchema.class))).thenThrow(RuntimeException.class);

        // act & assert
        exception.expect(FailedScrappingException.class);
        service.downloadProductListCsv(category, count);
    }
}
