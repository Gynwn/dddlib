/**
 *
 */
package org.dayatang.persistence.hibernate;

import org.dayatang.domain.Criteria;
import org.dayatang.domain.CriteriaQuery;
import org.dayatang.domain.QueryCriterion;
import org.dayatang.persistence.test.domain.Dictionary;
import org.dayatang.persistence.test.domain.DictionaryCategory;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;


/**
 *
 * @author yang
 */
public class CriteriaQueryTest extends AbstractIntegrationTest {

    private CriteriaQuery instance;

    private CriteriaQuery instance2;

    private DictionaryCategory gender;

    private DictionaryCategory education;

    private Dictionary male;

    private Dictionary female;

    private Dictionary unknownGender;

    private Dictionary undergraduate;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        instance = new CriteriaQuery(repository, Dictionary.class);
        instance2 = new CriteriaQuery(repository, DictionaryCategory.class);
        gender = createCategory("gender", 1);
        education = createCategory("education", 2);
        male = createDictionary("01", "男", gender, 100, "01");
        female = createDictionary("02", "女", gender, 150, "01");
        unknownGender = createDictionary("03", "未知", gender, 160, "01");
        undergraduate = createDictionary("01", "本科", education, 200, "05");
    }

    @Test
    public void testEq() {
        instance.eq("category", gender);
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(male, female).doesNotContain(undergraduate);
    }

    @Test
    public void testNotEq() {
        instance.notEq("category", gender);
        List<Dictionary> results = repository.find(instance);
        Dictionary dictionary = results.get(0);
        assertThat(dictionary.getCategory()).isEqualTo(education);
    }

    @Test
    public void testGe() {
        instance.ge("sortOrder", 150);
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(female, undergraduate).doesNotContain(male);
    }

    @Test
    public void testGt() {
        instance.gt("sortOrder", 150);
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(undergraduate).doesNotContain(male, female);
    }

    @Test
    public void testLe() {
        instance.le("sortOrder", 150);
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(male, female).doesNotContain(undergraduate);
    }

    @Test
    public void testLt() {
        instance.lt("sortOrder", 150);
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(male).doesNotContain(female, undergraduate);
    }

    @Test
    public void testEqProp() {
        instance.eqProp("code", "parentCode");
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(male).doesNotContain(female, undergraduate);
    }

    @Test
    public void testNotEqProp() {
        instance.notEqProp("code", "parentCode");
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(female, undergraduate).doesNotContain(male);
    }

    @Test
    public void testGtProp() {
        instance.gtProp("code", "parentCode");
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(female).doesNotContain(male, undergraduate);
    }

    @Test
    public void testGeProp() {
        instance.geProp("code", "parentCode");
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(male, female).doesNotContain(undergraduate);
    }

    @Test
    public void testLtProp() {
        instance.ltProp("code", "parentCode");
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(undergraduate).doesNotContain(male, female);
    }

    @Test
    public void testLeProp() {
        instance.leProp("code", "parentCode");
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(male, undergraduate).doesNotContain(female);
    }

    @Test
    public void testSizeEq() {
        instance2.sizeEq("dictionaries", 3);
        List<DictionaryCategory> results = repository.find(instance2);
        assertThat(results).contains(gender).doesNotContain(education);
    }

    @Test
    public void testSizeNotEq() {
        instance2.sizeNotEq("dictionaries", 3);
        List<DictionaryCategory> results = repository.find(instance2);
        assertThat(results).contains(education).doesNotContain(gender);
    }

    @Test
    public void testSizeGt() {
        instance2.sizeGt("dictionaries", 1);
        List<DictionaryCategory> results = repository.find(instance2);
        assertThat(results).contains(gender).doesNotContain(education);
    }

    @Test
    public void testSizeGe() {
        instance2.sizeGe("dictionaries", 3);
        List<DictionaryCategory> results = repository.find(instance2);
        assertThat(results).contains(gender).doesNotContain(education);
    }

    @Test
    public void testSizeLt() {
        instance2.sizeLt("dictionaries", 3);
        List<DictionaryCategory> results = repository.find(instance2);
        assertThat(results).contains(education).doesNotContain(gender);
    }

    @Test
    public void testSizeLe() {
        instance2.sizeLe("dictionaries", 3);
        List<DictionaryCategory> results = repository.find(instance2);
        assertThat(results).contains(gender, education);
    }

    @Test
    public void testIsEmpty() {
        DictionaryCategory empty = createCategory("a", 3);
        instance2.isEmpty("dictionaries");
        List<DictionaryCategory> results = repository.find(instance2);
        assertThat(results).contains(empty).doesNotContain(gender, education);
    }

    @Test
    public void testNotEmpty() {
        DictionaryCategory empty = createCategory("a", 3);
        instance2.notEmpty("dictionaries");
        List<DictionaryCategory> results = repository.find(instance2);
        assertThat(results).contains(gender, education).doesNotContain(empty);
    }

    @Test
    public void testContainsText() {
        instance.containsText("text", "科");
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(undergraduate).doesNotContain(male, female);
    }

    @Test
    public void testStartsWithText() {
        instance.startsWithText("text", "本");
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(undergraduate);

        instance = new CriteriaQuery(repository, Dictionary.class).startsWithText("text", "科");
        results = repository.find(instance);
        assertThat(results).doesNotContain(undergraduate);
    }

    @Test
    public void testInEntity() {
        Set<DictionaryCategory> params = new HashSet<DictionaryCategory>();
        params.add(education);
        params.add(gender);
        instance.in("category", params);
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(male, female, undergraduate);
    }

    @Test
    public void testInString() {
        Set<String> params = new HashSet<String>();
        params.add("男");
        params.add("女");
        instance.in("text", params);

        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(male, female).doesNotContain(undergraduate);
    }

    @Test
    public void testInNull() {
        Collection<Object> value = null;
        instance.in("id", value);
        List<Dictionary> results = repository.find(instance);
        assertThat(results).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInEmpty() {
        instance.in("id", Collections.EMPTY_LIST);
        List<Dictionary> results = repository.find(instance);
        assertThat(results).isEmpty();
    }

    @Test
    public void testNotInEntity() {
        Set<Long> params = new HashSet<Long>();
        params.add(male.getId());
        params.add(female.getId());
        instance.notIn("id", params);
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(undergraduate).doesNotContain(male, female);
    }

    @Test
    public void testNotInString() {
        Set<String> params = new HashSet<String>();
        params.add("男");
        params.add("女");
        instance.notIn("text", params);
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(undergraduate).doesNotContain(male, female);
    }

    @Test
    public void testNotInNull() {
        Collection<Object> value = null;
        instance.notIn("id", value);
        List<Dictionary> results = repository.find(instance);
        assertThat(results).isNotEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNotInEmpty() {
        instance.notIn("id", Collections.EMPTY_LIST);
        List<Dictionary> results = repository.find(instance);
        assertThat(results).isNotEmpty();
    }

    @Test
    public void testIsNull() {
        instance.isNull("description");
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(male, female, undergraduate);
    }

    @Test
    public void testNotNull() {
        instance.notNull("text");
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(male, female, undergraduate);
    }

    @Test
    public void testBetween() {
        instance.between("parentCode", "01", "02");
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(male, female).doesNotContain(undergraduate);
    }

    @Test
    public void testAnd() {
        QueryCriterion or = Criteria.or(Criteria.eq("code", "01"), Criteria.eq("code", "02"));
        instance.eq("category", gender).and(or);
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(male, female).doesNotContain(unknownGender, undergraduate);
    }

    @Test
    public void testOr() {
        QueryCriterion and = Criteria.and(Criteria.eq("code", "01"), Criteria.eq("category", gender));
        instance.eq("category", education).or(and);
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(male, undergraduate).doesNotContain(female);
    }

    @Test
    public void testNot() {
        instance.not(Criteria.eq("code", "01"));
        List<Dictionary> results = repository.find(instance);
        assertThat(results).contains(female).doesNotContain(male, undergraduate);
    }

    @Test
    public void testFindPaging() {
        createDictionary("08", "xyz", education, 150, "01");
        createDictionary("09", "xyy", education, 160, "02");
        instance.setFirstResult(1).setMaxResults(2);
        List<Dictionary> results = repository.find(instance);
        assertThat(results).hasSize(2);
    }

    @Test
    public void testFindOrder() {
        instance.asc("sortOrder");
        List<Dictionary> results = repository.find(instance);
        assertTrue(results.indexOf(male) < results.indexOf(female));
        assertTrue(results.indexOf(female) < results.indexOf(undergraduate));

        instance = new CriteriaQuery(repository, Dictionary.class).desc("sortOrder");
        results = repository.find(instance);
        assertTrue(results.indexOf(male) > results.indexOf(female));
        assertTrue(results.indexOf(female) > results.indexOf(undergraduate));
    }

    @Test
    public void testAlias() {
        List<Dictionary> results = repository.find(instance.eq("category.name", "education"));
        assertThat(results).contains(undergraduate);
    }

    private DictionaryCategory createCategory(String name, int sortOrder) {
        DictionaryCategory category = new DictionaryCategory();
        category.setName(name);
        category.setSortOrder(sortOrder);
        repository.save(category);
        repository.flush();
        return category;
    }

    private Dictionary createDictionary(String code, String text, DictionaryCategory category, int sortOrder,
            String parentCode) {
        Dictionary dictionary = new Dictionary(code, text, category);
        dictionary.setSortOrder(sortOrder);
        dictionary.setParentCode(parentCode);
        repository.save(dictionary);
        repository.flush();
        return dictionary;
    }
}
