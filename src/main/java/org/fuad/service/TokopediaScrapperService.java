package org.fuad.service;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.fuad.exception.FailedScrappingException;
import org.fuad.model.Product;
import org.fuad.scrapper.TokopediaScrapper;
import org.fuad.scrapper.TokopediaScrapper.Category;
/* here service for save the data to CSV File */
public class TokopediaScrapperService {

    private static final String UNDERSCORE = "_";
    private static final String PRODUCT = "Product";
    private static final String CSV_EXT = ".csv";

    private TokopediaScrapper scrapper;

    public TokopediaScrapperService() {
        scrapper = new TokopediaScrapper();
    }

    @VisibleForTesting
    TokopediaScrapperService(TokopediaScrapper scrapper) {
        this.scrapper = scrapper;
    }

    public String downloadProductListCsv(Category category, int count)
            throws FailedScrappingException {
        String filename = PRODUCT + UNDERSCORE + category.getName()
                + UNDERSCORE + System.currentTimeMillis() + CSV_EXT;
        List<Product> products = scrapper.extractProductList(category, count);

        CsvMapper csvMapper = new CsvMapper();
        csvMapper.enable(Feature.IGNORE_UNKNOWN);
        csvMapper.addMixIn(Product.class, Product.ProductFormat.class);
        CsvSchema schema = csvMapper.schemaFor(Product.class).withHeader();

        try {
            File file = new File(filename);
            csvMapper.writer(schema).writeValue(file, products);
            return filename;
        } catch (IOException | RuntimeException e) {
            throw new FailedScrappingException(e.getMessage());
        }
    }
}
