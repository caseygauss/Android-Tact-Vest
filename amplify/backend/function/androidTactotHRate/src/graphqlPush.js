module.exports = {
  mutation: `mutation createHRate(
    $input: CreateHRateInput!
    $condition: ModelHRateConditionInput
  ) {
    createHRate(input: $input) {
      currentRate
    }
  }
  `,
};