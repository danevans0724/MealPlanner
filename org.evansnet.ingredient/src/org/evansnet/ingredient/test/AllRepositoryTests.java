package org.evansnet.ingredient.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestRepository.class, TestRepositoryBuilder.class, TestRepositoryHelper.class })
public class AllRepositoryTests {

}
