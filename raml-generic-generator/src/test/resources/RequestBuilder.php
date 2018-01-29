<?php
declare(strict_types = 1);
/**
 * This file has been auto generated
 * Do not change it
 */

namespace Test\Request;

use Test\Client\Resource;
use Test\Client\ApiRequest;

class RequestBuilder extends Resource
{
    public function __construct()
    {
        parent::__construct('');
    }

    /**
     * @return Resource0
     */
    public function withProjectValue($project): Resource0 {
        $args = array_merge($this->getArgs(), ['project' => $project]);
        return new Resource0($this->getUri() . '/{project}', $args);
    }


    /**
     * @param string $method
     * @param string $uri
     * @param array $headers
     * @param $body
     * @param string $version
     * @return ApiRequest
     */
    final public function buildCustom(
        string $method,
        string $uri,
        array $headers = [],
        $body = null,
        string $version = '1.1'
    ) {
        return new ApiRequest($method, $uri, $headers, $body, $version);
    }

}
