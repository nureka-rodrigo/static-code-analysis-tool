/*
 *  Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.ballerina.scan.internal;

import io.ballerina.scan.Rule;
import io.ballerina.scan.RuleKind;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code CoreRule} contains the core static code analysis rules.
 *
 * @since 0.1.0
 * */
enum CoreRule {

    AVOID_CHECKPANIC(RuleFactory.createRule(1, "Avoid checkpanic", RuleKind.CODE_SMELL)),
    UNUSED_FUNCTION_PARAMETER(RuleFactory.createRule(2,
            "Unused function parameter", RuleKind.CODE_SMELL)),
    PUBLIC_NON_ISOLATED_FUNCTION_CONSTRUCT(RuleFactory.createRule(3,
            "Non isolated public function", RuleKind.CODE_SMELL)),
    PUBLIC_NON_ISOLATED_METHOD_CONSTRUCT(RuleFactory.createRule(4,
            "Non isolated public method", RuleKind.CODE_SMELL)),
    PUBLIC_NON_ISOLATED_CLASS_CONSTRUCT(RuleFactory.createRule(5,
            "Non isolated public class", RuleKind.CODE_SMELL)),
    PUBLIC_NON_ISOLATED_OBJECT_CONSTRUCT(RuleFactory.createRule(6,
            "Non isolated public object", RuleKind.CODE_SMELL)),
    OPERATION_ALWAYS_EVALUATES_TO_TRUE(RuleFactory.createRule(7,
            "This operation always evaluates to true", RuleKind.CODE_SMELL)),
    OPERATION_ALWAYS_EVALUATES_TO_FALSE(RuleFactory.createRule(8,
            "This operation always evaluates to false", RuleKind.CODE_SMELL)),
    OPERATION_ALWAYS_EVALUATES_TO_SELF_VALUE(RuleFactory.createRule(9,
            "This operation always evaluates to the same value", RuleKind.CODE_SMELL)),
    SELF_ASSIGNMENT(RuleFactory.createRule(10,
            "This variable is assigned to itself", RuleKind.CODE_SMELL)),
    UNUSED_PRIVATE_CLASS_FIELD(RuleFactory.createRule(11,
            "Unused class private fields", RuleKind.CODE_SMELL)),
    INVALID_RANGE_EXPRESSION(RuleFactory.createRule(12, 
    "Invalid range expression", RuleKind.CODE_SMELL));

    private final Rule rule;

    CoreRule(Rule rule) {
        this.rule = rule;
    }

    Rule rule() {
        return rule;
    }

    static List<Rule> rules() {
        List<Rule> coreRules = new ArrayList<>();
        for (CoreRule coreRule: CoreRule.values()) {
            coreRules.add(coreRule.rule());
        }
        return coreRules;
    }
}
